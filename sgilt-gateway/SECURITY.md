# sgilt-gateway — Sécurité

`sgilt-gateway` est le point d'entrée public unique de Sgilt (Spring Cloud
Gateway, WebMVC). Il reçoit le trafic derrière nginx/Cloudflare, applique CORS
et l'authentification JWT (resource server), puis route vers `sgilt-core` et
`sgilt-notifications` (voir `application.yml`, section `spring.cloud.gateway`).
Ce fichier documente les décisions de sécurité propres à ce module — pas de
duplication de `doc/ARCHITECTURE.md` (infra) ni de `sgilt-core/CLAUDE.md`
(conventions de code).

## Résolution de l'IP client

nginx résout l'IP réelle du client via son module `real_ip`, en faisant
confiance à l'en-tête `CF-Connecting-IP` de Cloudflare (plages IP Cloudflare
whitelistées dans `deploy/deploy-bundle/nginx/api.conf.template`), puis la
transmet à `sgilt-gateway` via l'en-tête `X-Real-IP`.

C'est la source à utiliser pour toute logique basée sur l'IP côté gateway —
`X-Forwarded-For` ne doit **pas** être utilisé directement pour ça : le
client peut y insérer ses propres valeurs avant que nginx n'y ajoute la
sienne (`proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for` fait un
append, pas un remplacement). `X-Real-IP`, lui, est toujours écrasé par
nginx avec l'IP déjà validée par `real_ip` — c'est le seul hop de confiance
entre le client et cette gateway.

## Rate limiting — `POST /api/v1/onboarding`

`InitOnboardingRateLimitFilter` (`ratelimit/InitOnboardingRateLimitFilter.java`)
protège l'unique endpoint public de la plateforme n'exigeant aucune
authentification (`POST /api/v1/onboarding`, alias `/init-onboarding` dans le
brief de sécurisation) contre le spam/abus.

**Algorithme : fenêtre fixe (fixed window).**
Le temps est découpé en fenêtres de durée fixe (10 min). Pour une clé donnée
(`IP:init-onboarding`), on compte les requêtes depuis le début de la fenêtre
courante ; au-delà du seuil (5), on rejette en `429` jusqu'à expiration de la
fenêtre, puis le compteur repart à zéro. C'est l'algorithme le plus simple
qui existe (par opposition à sliding window ou token bucket, plus lisses
mais plus complexes) — suffisant pour un endpoint à faible volume comme
celui-ci.

**Pourquoi dans `gateway`, et pas dans `core` :**
`gateway` est la première pièce de code Java à voir la requête brute, avant
tout routing vers `sgilt-core`. Rejeter ici, avant que la requête n'atteigne
le backend, garantit qu'un dépassement de seuil ne produit **aucun effet de
bord** : pas de ligne `Onboarding` en base, pas d'email envoyé. Si le
compteur vivait dans `core`, il faudrait laisser la requête traverser le
réseau jusqu'au backend pour se faire rejeter — ce qui annule l'intérêt du
filtre.

**Pourquoi en mémoire, et pas en base :**
Le compteur (`ConcurrentHashMap<String, WindowCounter>`) vit dans le process
JVM de `gateway`, purgé toutes les 5 min par une méthode `@Scheduled` pour ne
pas grossir indéfiniment. Ce n'est pas un batch de nettoyage BDD — rien
n'est persisté, rien ne vient de la base. C'est un état transitoire propre à
l'instance gateway.

Le cadencement du batch (5 min) est volontairement plus court que la fenêtre
(10 min), mais les deux durées sont indépendantes : le batch réutilise la
même condition d'expiration (`>= WINDOW_DURATION`) que la vérification faite
à chaque requête dans `doFilterInternal`, donc il **épargne toujours les
fenêtres encore en cours** — il ne fait que réclamer la mémoire des entrées
déjà expirées côté logique (IPs qui ont cessé d'appeler l'endpoint). Une
cadence plus rapide que la fenêtre borne juste le délai avant réclamation
mémoire d'une entrée abandonnée (jusqu'à ~5 min de plus après son expiration
réelle) ; elle n'a aucun effet sur la correction du rate-limiting lui-même.

**Deux mécanismes de nettoyage distincts, à ne pas confondre :**
- **Rafraîchissement passif** (dans `doFilterInternal`, à chaque requête) :
  si une IP qui *revient* a une fenêtre expirée, `compute()` la remplace par
  une neuve (`windowStart=now, count=1`). Aucun cleanup nécessaire pour ce
  cas — c'est intrinsèque à la logique de fenêtre fixe.
- **Cleanup actif** (`@Scheduled`, toutes les 5 min) : couvre le cas d'une IP
  qui a fait ses quelques requêtes puis *ne revient jamais*. Une entrée
  orpheline comme celle-ci n'est plus jamais relue par personne, donc rien
  ne la "touche" pour déclencher une auto-suppression — une
  `ConcurrentHashMap` n'a pas de TTL natif, une entrée expirée n'y disparaît
  pas toute seule. Sans ce balayage périodique, ces entrées orphelines
  s'accumuleraient indéfiniment (fuite mémoire lente).

**Limites connues :**
- **Clé = IP** : plusieurs utilisateurs légitimes derrière la même IP
  publique (NAT d'entreprise, wifi partagé, CGNAT mobile) partagent le même
  compteur et peuvent se bloquer mutuellement plus vite que prévu. Compromis
  inhérent à un endpoint anonyme, sans identité utilisateur à ce stade pour
  affiner la clé.
- **État non partagé entre instances** : si `sgilt-gateway` est un jour
  scalé à plusieurs replicas derrière un load-balancer, chaque instance a son
  propre compteur — un attaquant pourrait contourner la limite en tombant sur
  des instances différentes. Non pertinent tant que `gateway` tourne en
  instance unique (cas actuel, voir `doc/ARCHITECTURE.md`) ; à revisiter
  (Redis ou équivalent partagé) si ça change.
- **Reset au redémarrage** : un déploiement remet tous les compteurs à zéro.

**Portée volontairement limitée :**
Le filtre ne s'applique qu'à `POST /api/v1/onboarding` exact
(`shouldNotFilter`), pas aux autres routes du préfixe `/api/v1/onboarding/**`
(`/verify`, `/confirm-account`) ni à aucun autre endpoint de l'API — décision
du chantier de sécurisation initial, pas une limite technique. Étendre ce
pattern à un autre endpoint est possible (dupliquer le filtre avec une autre
clé de scope) mais doit être une décision délibérée, pas un effet de bord.

## CORS

`SecurityConfig.corsConfigurationSource()` — une seule origine autorisée
(`APP_URL`, positionnée par environnement : `staging.sgilt.fr` en staging,
`sgilt.alsace` en prod), pas de wildcard, `allowCredentials` jamais activé.
S'applique à toutes les routes, y compris les publiques.
