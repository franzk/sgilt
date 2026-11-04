# Billetterie — flow technique de l'achat

> Documentation technique de l'achat de places pour un événement public (`/e/[slug]`), du clic
> sur « Acheter mes billets » jusqu'à l'e-billet. Le paiement passe par Stripe, derrière le
> service `sgilt-pay`.
>
> Vocabulaire : on achète des **places** (ce que compte la jauge) ; une commande payée donne **un
> e-billet** (un seul QR code, valable pour toutes ses places).

## Vue d'ensemble

| Brique | Contenu                                                                                   | État                  |
|--------|-------------------------------------------------------------------------------------------|-----------------------|
| 5      | Création de la commande côté back (`POST /api/v1/commandes`), blocage des places          | Fait                  |
| 1a     | `sgilt-pay` crée le PaymentIntent Stripe, le core renvoie son secret client au front      | Fait (2026-10-31)     |
| 1b     | Payment Element Stripe dans l'écran de paiement, confirmation du paiement depuis le front | Fait (2026-10-31)     |
| 2      | Webhook Stripe → `sgilt-pay` → RabbitMQ (queue `payment.succeeded`) → le core passe la commande `PAID`, alerte de survente à l'équipe Sgilt | Fait (2026-11-02)     |
| 3      | `GET /commandes/{id}` et écran de confirmation sans état front                            | Fait (2026-11-01)     |
| —      | Anti-abus : limite de débit par IP (gateway), plafond de 10 places par commande           | Fait (2026-11-03/04)  |
| —      | E-billet : code d'entrée au passage `PAID`, `GET /commandes/{id}/e-billet`, page « Mon e-billet » avec QR | Fait (2026-11-03)     |
| —      | E-mail à l'acheteur au passage `PAID`, avec le lien vers son e-billet                     | Fait (2026-11-03)     |

Composants :

| Composant       | Rôle                                                                                                       |
|-----------------|------------------------------------------------------------------------------------------------------------|
| `sgilt-front`   | Parcours d'achat (`/e/[slug]/commande`, `/paiement`, `/confirmation`, `/e-billet`, tous en `ssr: false`), widget Stripe, QR code |
| `sgilt-gateway` | Routes publiques `/api/v1/commandes/**` (`permitAll` : on achète sans compte) et `/webhooks/stripe`, limite de débit par IP |
| `sgilt-core`    | Commandes, blocage des places, appel à `sgilt-pay`, passage `PAID`, code d'entrée, e-billet, e-mails        |
| `sgilt-pay`     | Seul service à parler à Stripe (clé secrète). Port 5032, réseau interne, sans base                         |
| RabbitMQ        | `payment.succeeded` (`sgilt-pay` → core), `mail.send` (core → `sgilt-mailer`)                              |
| `sgilt-mailer`  | E-mail d'e-billet à l'acheteur, alerte de survente à l'équipe Sgilt                                        |
| Stripe          | PaymentIntents API, Payment Element (Stripe.js), webhooks                                                  |

---

## Parcours d'achat côté front

Le brouillon de commande (événement, quantité, acheteur, étape) vit dans le sessionStorage
(`useTicketOrder`) : il survit au rechargement, pas à la fermeture de l'onglet. Le middleware
`ticketing` n'ouvre chaque écran qu'à son étape.

```mermaid
stateDiagram-v2
    [*] --> NONE
    NONE --> STARTED: « Acheter mes billets » (fiche /e/[slug])
    STARTED --> ORDERED: validation de l'écran de commande (front seul, aucun appel back)
    ORDERED --> PAID: paiement confirmé par Stripe (écran de paiement)
    PAID --> STARTED: nouvel achat
```

| Écran                     | Ouvert si                                               |
|---------------------------|---------------------------------------------------------|
| `/e/[slug]/commande`      | billetterie achetable **et** étape `STARTED` ou `ORDERED` |
| `/e/[slug]/paiement`      | billetterie achetable **et** étape `ORDERED`            |
| `/e/[slug]/confirmation`  | `?commande={id}` dans l'URL, quelle que soit l'étape (même si l'événement est devenu complet) |
| `/e/[slug]/e-billet`      | `?commande={id}` dans l'URL, même règle ; une commande inconnue ou pas payée renvoie à la fiche |

L'écran de commande ne crée rien côté back : la commande n'existe qu'au clic sur « Payer ».

---

## Séquence du paiement (briques 5, 1a, 1b)

Le Payment Element est monté **sans PaymentIntent** (mode différé : montant, devise, moyens de
paiement). Le PaymentIntent n'est créé qu'au clic sur « Payer », en même temps que la commande.

```mermaid
sequenceDiagram
    actor Acheteur
    participant Front as sgilt-front (paiement.vue)
    participant StripeJS as Stripe.js (Payment Element)
    participant GW as sgilt-gateway
    participant Core as sgilt-core
    participant DB as PostgreSQL
    participant Pay as sgilt-pay
    participant Stripe as API Stripe

    Note over Front,StripeJS: Au chargement de la page
    Front->>StripeJS: loadStripe(clé publique) puis elements({ mode: 'payment', amount, currency: 'eur', allowedPaymentMethodTypes: ['card'] })
    StripeJS-->>Acheteur: formulaire carte (+ Apple Pay / Google Pay si disponibles)

    Acheteur->>Front: clic « Payer »
    Front->>Front: CGV cochées ? sinon erreur, rien n'est envoyé
    Front->>StripeJS: elements.submit()
    StripeJS-->>Front: saisie valide (sinon erreurs dans le widget, on s'arrête)

    Front->>GW: POST /api/v1/commandes { slug, quantity, firstName, lastName, email }
    GW->>Core: (permitAll)
    Core->>DB: SELECT billetterie FOR UPDATE (verrou pessimiste)
    Core->>Core: billetterie OPEN ? (sinon 400) places restantes suffisantes ? (sinon 409)
    Core->>DB: INSERT commandes (PENDING, expires_at = now + 10 min)
    Core->>Pay: POST /internal/payment-intents { commandeId, amountCents }
    Pay->>Stripe: PaymentIntent.create(amount, eur, allowed_payment_method_types=[card], metadata.commandeId)<br/>Idempotency-Key = commandeId
    Stripe-->>Pay: PaymentIntent (client_secret)
    Pay-->>Core: 201 { clientSecret }
    Core->>DB: commit (libère le verrou)
    Core-->>Front: 201 { id, quantity, totalCents, expiresAt, clientSecret }

    Front->>Front: attachCommande(id)
    Front->>StripeJS: confirmPayment({ elements, clientSecret, return_url, redirect: 'if_required' })
    StripeJS->>Stripe: confirmation (carte)
    opt Carte 3D Secure
        Stripe-->>Acheteur: fenêtre d'authentification (modale, pas de redirection)
        Acheteur->>Stripe: valide
    end
    Stripe-->>StripeJS: PaymentIntent succeeded
    StripeJS-->>Front: pas d'erreur
    Front->>Front: markPaid()
    Front->>Acheteur: /e/[slug]/confirmation?commande={id}
```

### Écran de confirmation (brique 3)

Le statut vient du back, pas du sessionStorage : l'URL suffit, même au retour d'une redirection
(`return_url`) ou dans un autre onglet. Stripe confirme le paiement au front avant le webhook : la
commande peut encore être `PENDING` à l'arrivée sur l'écran.

```mermaid
sequenceDiagram
    participant Front as sgilt-front (confirmation)
    participant Core as sgilt-core

    loop toutes les 2 s, 30 s au plus
        Front->>Core: GET /api/v1/commandes/{id}
        Core-->>Front: { id, status, quantity, unitPriceCents, totalCents }
    end
    alt PAID
        Front->>Front: confirmation (places, montants, bouton « Voir mon e-billet »)
    else toujours PENDING au bout de 30 s
        Front->>Front: « la confirmation peut prendre quelques minutes, rechargez la page »
    else 404
        Front->>Front: retour à la fiche de l'événement
    end
```

Une erreur passagère (réseau, 5xx) ne coupe pas l'attente.

### Points clés

- **Le verrou de la billetterie** (`BilletterieService.lockOpenBilletterie`, `PESSIMISTIC_WRITE`)
  sérialise les acheteurs d'un même événement : le décompte des places et l'insertion de la
  commande sont atomiques. Il est tenu pendant l'appel à Stripe (environ 1 s).
- **Échec de `sgilt-pay`** (Stripe indisponible, clé invalide) : `sgilt-pay` répond 502, l'appel
  du core lève une exception, la transaction est annulée. Aucune commande ne reste sans
  PaymentIntent.
- **Idempotence** : la clé d'idempotence Stripe est l'id de la commande. Un second appel pour la
  même commande renvoie le même PaymentIntent.
- **Le secret client** n'est pas stocké : il n'est que dans la réponse du `POST`, le front s'en
  sert une fois pour confirmer le paiement.
- **Moyens de paiement** : carte uniquement, Apple Pay et Google Pay en découlent (sur les
  appareils compatibles, en HTTPS, sur un domaine déclaré chez Stripe). Même liste côté front
  (`allowedPaymentMethodTypes`) et côté `sgilt-pay` (`allowed_payment_method_types`). Link est
  exclu (il redemandait les coordonnées déjà saisies) et masqué explicitement dans le widget
  (`wallets: { link: 'never' }`). Pas de moyen de paiement différé (SEPA…) ni à redirection
  (PayPal…) pour l'instant.

### Cas d'erreur

| Cas                                   | Où                     | Ce que voit l'acheteur                                         | Commande                       |
|---------------------------------------|------------------------|----------------------------------------------------------------|--------------------------------|
| CGV non cochées                       | front                  | erreur sous la case                                            | aucune                         |
| Saisie carte invalide                 | `elements.submit()`    | erreurs dans le widget + message Stripe sous le bouton         | aucune                         |
| Plus assez de places                  | core, 409              | « Il ne reste plus assez de places… »                          | aucune                         |
| Plus de 10 places                     | front (core : 400)     | « Vous pouvez réserver jusqu'à 10 places par commande. » au clic sur « + » | aucune               |
| Trop de commandes depuis cette IP     | gateway, 429           | « Trop de tentatives… réessayer dans quelques minutes. »       | aucune                         |
| Billetterie fermée / introuvable      | core, 400 / 404        | « La billetterie de cet événement n'est plus ouverte. »        | aucune                         |
| Core ou `sgilt-pay` en erreur         | 5xx                    | « Une erreur est survenue, merci de réessayer. »               | aucune (rollback)              |
| Carte refusée                         | `confirmPayment`       | message Stripe (en français) sous le bouton                    | reste `PENDING`                |
| 3D Secure échoué                      | `confirmPayment`       | message générique                                              | reste `PENDING`                |

Après un refus, un nouvel essai crée une **nouvelle** commande (et un nouveau PaymentIntent).
L'ancienne garde ses places jusqu'à son expiration.

---

## Blocage des places

```
places restantes = capacité − Σ quantité (PAID) − Σ quantité (PENDING et expires_at > now)   (minimum 0)
```

- Une commande `PENDING` bloque ses places **10 minutes** (`CommandeService.COMMANDE_HOLD_DURATION`).
- Au-delà, elle ne compte plus : ses places se libèrent d'elles-mêmes, sans tâche planifiée. La
  commande reste `PENDING` en base.
- **Paiement arrivé après l'expiration** (acheteur lent, 3D Secure long) : la commande passe
  `PAID` quand même, le paiement a eu lieu. Si ses places ont été reprises entre-temps, la jauge
  est dépassée (survente, rarissime) : pas de remboursement automatique, un warn dans les logs et
  un e-mail d'alerte à l'équipe Sgilt (`AdminMailerService.sendOversoldAlert`, adresse
  `ADMIN_EMAIL`), qui décide : rembourser depuis le dashboard Stripe, ou accepter la survente.
- Statuts (`commande_status`) : `PENDING`, `PAID`, `CANCELLED`. **`CANCELLED` n'est produit par
  aucun code aujourd'hui** : il est réservé (annulation, libération anticipée).

---

## Après le paiement : webhook, passage `PAID`, e-mail (brique 2)

```mermaid
sequenceDiagram
    participant Stripe as API Stripe
    participant GW as nginx-api / gateway
    participant Pay as sgilt-pay
    participant MQ as RabbitMQ
    participant Core as sgilt-core
    participant DB as PostgreSQL
    participant Mailer as sgilt-mailer
    actor Acheteur

    Stripe->>GW: POST /webhooks/stripe (payment_intent.succeeded)
    GW->>Pay: route publique (seule route de sgilt-pay exposée)
    Pay->>Pay: vérifie la signature Stripe (tient lieu d'authentification)
    Pay->>MQ: payment.succeeded { commandeId (metadata), paymentIntentId }
    Pay-->>Stripe: 200
    MQ->>Core: PaymentSucceededListener
    Core->>DB: commande déjà PAID ? → rien (événement rejoué), fin
    Core->>DB: commande → PAID + code d'entrée tiré
    opt places payées > jauge (survente)
        Core->>MQ: mail.send ADMIN_OVERSOLD_ALERT_EMAIL → équipe Sgilt (ADMIN_EMAIL)
    end
    Core->>MQ: mail.send COMMANDE_E_BILLET_EMAIL → acheteur
    MQ->>Mailer: consomme
    Mailer->>Acheteur: « Votre e-billet pour … » (lien vers l'e-billet)
```

- **Idempotence** : `CommandeService.markPaid` ne renvoie la commande que si elle vient de passer
  `PAID` (`Optional` vide sinon) ; un événement Stripe rejoué ne tire pas de nouveau code et
  n'envoie pas de second e-mail.
- **Orchestration dans le listener** : `PaymentSucceededListener` enchaîne `markPaid`, le slug et
  les infos de l'événement (`PageEvenementService`) puis l'envoi (`CommandeMailerService`).
  `CommandeService` ne peut pas appeler `PageEvenementService` (`BilletterieService` dépend déjà de
  `CommandeService`).
- **E-mail d'e-billet** (`COMMANDE_E_BILLET_EMAIL`) : prénom, titre, « valable pour N
  personne(s) » (accordé par le core), date et horaires (`EventDateLabelUtils`, même rendu que le
  front — doublon assumé), lieu, ville, lien `${PUBLIC_SITE_URL}/e/{slug}/e-billet?commande={id}`.
  Pas de QR dans l'e-mail : il est derrière le lien.
- **Échec d'envoi** : journalisé, pas relancé ; la commande reste payée. Un événement sans page
  (impossible en pratique) n'envoie rien, avec une erreur dans les logs. Lieu ou ville absents :
  envoyés vides.
- En local, le conteneur `sgilt-stripe-cli` (`stripe listen`) fait le pont depuis Stripe.

---

## E-billet

Une commande payée a **un** e-billet : un seul QR code, présenté à l'entrée, qui fait entrer toutes
ses places.

- **Code d'entrée** : tiré au passage `PAID` (`EntryCodeUtils.newCode`, 128 bits aléatoires,
  base64url sans padding, 22 caractères), colonne `commandes.code` (unique, V124).
- **Contenu du QR** : `SGILT:C:<code>` (préfixe de type, **sans lien**), fabriqué par le back
  (`EntryCodeUtils.commandeQrContent`). Le futur lecteur sera une page Sgilt.
- **Route** : `GET /api/v1/commandes/{id}/e-billet` (publique) → `EBilletDto` : titre, image,
  type d'événement (image par défaut côté front), `evenementInfos` (tag et sous-titre de la page,
  date, horaires, lieu, ville, infos pratiques), nombre de places, contenu du QR. 404 si la
  commande est inconnue **ou pas payée**.
- **Page** `/e/[slug]/e-billet?commande={id}` : même hero que la fiche (`SgiltHero`), **sans** le
  bouton partager (il partagerait l'URL, donc l'e-billet) ; QR dessiné dans le navigateur
  (`qrcode`, correction d'erreur M, 12rem) ; « Valable pour N personne(s) » ; infos pratiques ;
  contact. Commande inconnue ou pas payée → fiche ; toute autre erreur → « Réessayer » (l'acheteur
  est peut-être à l'entrée).
- **Accès** : l'id de la commande seul (UUID v4, non devinable), sans compte. L'URL ne doit pas
  fuiter :
  - `X-Robots-Tag: noindex, nofollow, noarchive` (routeRule de `nuxt.config.ts`) — un en-tête, la
    balise meta serait inutile en `ssr: false` ;
  - `Referrer-Policy: no-referrer` sur l'e-billet **et** la confirmation (on passe de l'une à
    l'autre sans recharger : c'est l'en-tête de la confirmation qui s'applique) ;
  - pas de `Disallow` dans `robots.txt` (il empêcherait les moteurs de lire le `noindex`) ; page
    absente du sitemap (route dynamique) et liée nulle part publiquement.
- **Limite** : le middleware exige une fiche avec billetterie ; si l'événement perd sa page ou sa
  billetterie, ses e-billets ne s'affichent plus.

---

## Anti-abus

`POST /api/v1/commandes` est public : sans garde-fou, n'importe qui bloquerait des places 10 minutes
sans payer et créerait des PaymentIntents en masse.

- **Limite de débit** : **20 commandes par IP sur 10 minutes** dans la gateway (`RateLimitFilter`,
  règle `COMMANDES` de `RateLimitRules`, IP réelle via `X-Real-IP`) ; au-delà, 429 avant le core,
  message « trop de tentatives » côté front. Limite large : réessais après une carte refusée,
  acheteurs derrière la même IP le soir de l'événement. Les `GET` (confirmation, e-billet) ne sont
  pas limités.
- **Plafond par commande** : **10 places** (`Commande.MAX_PLACES_PER_COMMANDE`), vérifié par
  `@Max` sur `CreateCommandeRequest` (400) et servi au front dans la billetterie publique
  (`TicketingDto.maxPlacesPerCommande`) : une seule source. Au clic sur « + » au-delà, message
  neutre ; s'il reste moins de 10 places, c'est le message des places restantes qui s'affiche.

---

## Reporté

- **Scan à l'entrée** : lecteur de QR dans une page Sgilt (équipe connectée). Points ouverts : qui
  scanne, `scanned_by`, entrées partielles d'un groupe avec un seul QR.
- **Remboursement / annulation** → commande `CANCELLED` et e-billet invalidé (avec le scan).
- **Libération anticipée des places** : `payment_intent.payment_failed` n'est pas un abandon
  (l'acheteur peut réessayer sur le même PaymentIntent) et l'abandon n'émet aucun événement.
  Libérer plus tôt voudra dire annuler nous-mêmes le PaymentIntent (commande remplacée ou expirée)
  et passer la commande `CANCELLED`.
- **PDF et billets individuels** (un QR par place) : avec le PDF joint à l'e-mail.
- **E-mail admin « nouvelle réservation »**, sur le socle `AdminMailerService`.
- **E-mails publiés avant le commit** : l'alerte de survente et l'e-billet partent dans la
  transaction du listener ; si elle échouait après l'envoi, Rabbit rejouerait le message et un
  second e-mail partirait. Improbable, non traité.

---

## Configuration

| Variable                              | Service       | Local                                                       | Staging / prod                         |
|---------------------------------------|---------------|-------------------------------------------------------------|----------------------------------------|
| `PAY_URL`                             | `sgilt-core`  | défaut `http://localhost:5032`                              | `http://sgilt-pay-${ENV}:5032`         |
| `STRIPE_SECRET_KEY`                   | `sgilt-pay`   | `application-local-secrets.yml` (gitignoré, profil `local`) | variable d'environnement               |
| `STRIPE_WEBHOOK_SECRET`               | `sgilt-pay`   | `application-local-secrets.yml`, secret de `stripe listen` (conteneur `sgilt-stripe-cli`) | secret de l'endpoint déclaré dans le dashboard |
| `NUXT_PUBLIC_STRIPE_PUBLISHABLE_KEY`  | `sgilt-front` | `sgilt-front/.env` (relancer `npm run dev` après modif)     | variable d'environnement               |
| `ADMIN_EMAIL`                         | `sgilt-core`  | `admin@sgilt.local` (`application-local.yml`, arrive dans MailHog) | secret GitHub, propre à l'environnement, sans défaut |
| `PUBLIC_SITE_URL`                     | `sgilt-core`  | défaut `http://localhost:3000`                              | déjà en place (liens des e-mails)      |

- Clés `sk_test` / `pk_test` en local et en staging ; clés live seulement après activation du
  compte Stripe.
- Avant la prod : déclarer le domaine pour Apple Pay, libellé de relevé bancaire, intégrer
  `sgilt-pay` aux docker-compose et à la CI de déploiement.
- Staging garde des clés de test en permanence. Cible : une sandbox Stripe dédiée (séparée du poste
  de dev, pour que les webhooks ne se mélangent pas). **Pour l'instant**, le compte n'étant pas
  activé (pas de sandbox possible), staging utilise le **Test mode** du compte, partagé avec le
  local : un paiement local est aussi envoyé à staging et inversement ; le core qui ne connaît pas
  la commande lève « commande introuvable » et le message finit dans `payment.succeeded.dlq` (bruit
  sans effet). Après activation : créer la sandbox « Sgilt staging » et remplacer les trois secrets
  Stripe de l'environnement GitHub `staging`.
- Secrets GitHub de l'environnement (vérifiés par le job `validate` de `deploy.yml`) :
  `ADMIN_EMAIL`, `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `STRIPE_PUBLISHABLE_KEY` (cf.
  `deploy/docs/SETUP.md`, 4.3).

### Sécurité (avant la prod)

- **Anti-abus** : fait, voir [Anti-abus](#anti-abus).
- **Clé restreinte** : en prod, `STRIPE_SECRET_KEY` reçoit une clé restreinte (`rk_live_…`) limitée
  aux PaymentIntents (écriture) et à ce que lit le webhook, plutôt que la clé secrète complète. Une
  fuite ne permet alors ni remboursement ni lecture des clients.
- **Réseau Docker dédié au paiement** : les services existants gardent leur réseau à plat
  (`sgilt-network-${ENV}`) ; un second réseau `payment` relie :
  - `sgilt-pay`, branché **uniquement** sur `payment` ;
  - `sgilt-core`, sur les deux réseaux (création des PaymentIntents) ;
  - `sgilt-gateway`, sur les deux réseaux, uniquement pour faire suivre `/webhooks/stripe` (liste
    blanche : jamais `/internal/...`).

  `/internal/payment-intents` n'a pas d'authentification : seuls le core et la gateway peuvent
  l'atteindre. Le webhook est protégé par la signature Stripe (secret `whsec_…` en variable
  d'environnement, horodatage contre le rejeu) et le passage `PAID` est idempotent.

### Cartes de test

| Carte                 | Résultat                       |
|-----------------------|--------------------------------|
| `4242 4242 4242 4242` | succès direct                  |
| `4000 0025 0000 3155` | 3D Secure (valider ou échouer) |
| `4000 0000 0000 0002` | refusée                        |

Date future quelconque, CVC quelconque.
