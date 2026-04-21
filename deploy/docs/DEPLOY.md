# SGILT — Pipeline de déploiement

Ce document décrit le fonctionnement du workflow `.github/workflows/deploy.yml`.

---

## Paramètres

Le workflow se lance manuellement depuis **Actions → Build & Deploy → Run workflow**.

| Paramètre     | Options                     | Description                                        |
|---------------|-----------------------------|----------------------------------------------------|
| `environment` | `staging`, `production`     | Détermine les secrets et variables GitHub utilisés |
| `mode`        | `update` *(défaut)*, `init` | Voir détail ci-dessous                             |

---

## Structure du workflow

Le workflow est composé de trois jobs séquentiels :

```
validate → build → deploy
```

### Job 1 — validate

Vérifie que tous les secrets et variables GitHub de l'environnement cible sont renseignés.
Échoue immédiatement en listant tout ce qui manque, avant de consommer du temps de build.

### Job 2 — build

Construit et pousse les images Docker sur GitHub Container Registry (GHCR).

**Optimisation :** Pour chaque image, le workflow vérifie si le tag existe déjà sur GHCR.
Si oui, le build est ignoré — l'image existante sera utilisée telle quelle.

Les tags sont dérivés des fichiers `VERSION` à la racine de chaque service :

| Service             | Fichier VERSION             | Format du tag             |
|---------------------|-----------------------------|---------------------------|
| `sgilt-front`       | `sgilt-front/VERSION`       | `{version}`               |
| `sgilt-core`        | `sgilt-core/VERSION`        | `{version}`               |
| `sgilt-gateway`     | `sgilt-gateway/VERSION`     | `{version}`               |
| `sgilt-mailer`      | `sgilt-mailer/VERSION`      | `{version}`               |
| `sgilt-smtp-bridge` | `sgilt-smtp-bridge/VERSION` | `{version}`               |
| `sgilt-keycloak`    | `sgilt-keycloak/VERSION`    | `{environment}-{version}` |

> Keycloak est taggué avec l'environnement car son image embarque le realm configuré pour cet environnement.

### Job 3 — deploy

Génère le fichier `.env` depuis les secrets et variables GitHub, copie les fichiers de déploiement sur le serveur via SCP, puis lance `deploy.sh` via SSH.

---

## Mode `update` — déploiement standard

Cas d'usage : nouvelle version d'un ou plusieurs services, ou changement de configuration.

**Ce qui se passe :**

1. `validate` — vérifie les secrets/variables
2. `build` — reconstruit uniquement les images dont le tag a changé
3. `deploy` :
   - Génère `.env` depuis GitHub (versions, URLs, ports, secrets SMTP/DB)
   - Copie `.env` et les fichiers de déploiement sur le serveur
   - Lance `docker compose pull && up -d`

**`.env.secrets` n'est pas touché.** Le fichier existant sur le serveur (créé à l'init) est préservé.

---

## Mode `init` — premier déploiement ou recréation du realm

Cas d'usage : premier déploiement d'un environnement, ou recréation complète du realm Keycloak suite à une modification du template.

> ⚠️ Relancer en mode `init` recrée l'image Keycloak et génère de nouveaux secrets applicatifs.
> Utiliser uniquement sur un environnement vierge ou après une suppression volontaire des données.

**Ce qui se passe :**

1. `validate` — vérifie les secrets/variables

2. `build` :
   - **Génère le realm Keycloak** depuis le template (`render-realm.sh`) en substituant l'`APP_URL`
   - **Construit l'image `sgilt-keycloak`** avec le realm embarqué et la pousse sur GHCR (le client `sgilt-admin` démarre avec un secret aléatoire généré par Keycloak à l'import)
   - Reconstruit les autres images si nécessaire

3. `deploy` :
   - Génère `.env` depuis GitHub
   - Copie `.env` et les fichiers de déploiement sur le serveur
   - Lance `deploy.sh init` via SSH, qui s'exécute en 3 phases sur le serveur :

   **Phase 1** — Démarre uniquement Keycloak (et sa DB) avec le flag `--import-realm`. Le realm est importé, le client `sgilt-admin` est créé avec un secret aléatoire.

   **Phase 2** — Attend que Keycloak soit prêt, puis :
   - Génère `KC_ADMIN_CLIENT_SECRET` et `CONFIRMATION_TOKEN_SECRET` via `openssl rand -hex 32`
   - Écrase le secret du client `sgilt-admin` dans Keycloak via `kcadm.sh`
   - Écrit `.env.secrets` sur le serveur

   **Phase 3** — Démarre tous les conteneurs restants (backend, frontend). `sgilt-core` démarre avec le `KC_ADMIN_CLIENT_SECRET` correct chargé depuis `.env.secrets`.

---

## Fichiers d'environnement sur le serveur

Le déploiement utilise deux fichiers distincts dans le répertoire de déploiement :

| Fichier        | Contenu                                               | Cycle de vie                                    |
|----------------|-------------------------------------------------------|-------------------------------------------------|
| `.env`         | Versions, URLs, ports, SMTP, KC_DB_PASSWORD, etc.     | Régénéré à chaque run depuis GitHub             |
| `.env.secrets` | `KC_ADMIN_CLIENT_SECRET`, `CONFIRMATION_TOKEN_SECRET` | Généré une seule fois à l'`init`, jamais écrasé |

`deploy.sh` charge les deux fichiers au démarrage. Si `.env.secrets` n'existe pas (environnement non initialisé), le démarrage échoue sur les variables manquantes.

> **Disaster recovery :** Si le serveur est recréé, relancer en mode `init` recrée `.env.secrets`.
> `CORE_DB_PASSWORD` est quant à lui conservé dans les secrets GitHub pour permettre la restauration d'un dump PostgreSQL existant.

---

## Déployer une nouvelle version

1. Modifier le fichier `VERSION` du service concerné
2. Committer sur `main`
3. Lancer le workflow en mode `update`

Le workflow détectera que le nouveau tag n'existe pas sur GHCR et reconstruira uniquement ce service.

---

## Rollback

1. Remettre l'ancienne version dans le fichier `VERSION`
2. Committer sur `main`
3. Lancer le workflow en mode `update`

L'ancienne image existe déjà sur GHCR — le build est ignoré, le déploiement est immédiat.
