# sgilt-r2-mock

Mock local de Cloudflare R2 (S3-compatible) — **développement uniquement**.

Simule les deux interfaces exposées par R2 en staging/prod : l'API S3 pour les uploads
(appelée par `sgilt-core` via le AWS SDK) et la livraison publique des fichiers
(appelée par le front via `NUXT_PUBLIC_IMAGE_BASE_URL`).

## Endpoints

| Méthode | Route | Description |
|---|---|---|
| `PUT` | `/{bucket}/{key}` | Upload d'un fichier (corps binaire brut, S3 path-style) |
| `DELETE` | `/{bucket}/{key}` | Suppression d'un fichier |
| `GET` | `/{key}` | Livraison d'un fichier (ex. `/uploads/abc.jpg`, `/bank/mariage.jpg`) |

## Images de banque

Les images de banque sont des photos génériques pré-committées dans `storage/bank/`.
Elles sont servies directement via le `GET /{key}` sans aucune configuration.

```
storage/bank/
  mariage.jpg         → GET /bank/mariage.jpg
  anniversaire.jpg    → GET /bank/anniversaire.jpg
  soiree_privee.jpg   → GET /bank/soiree_privee.jpg
  fete_entreprise.jpg → GET /bank/fete_entreprise.jpg
  autre.jpg           → GET /bank/autre.jpg
```

> En staging/prod, ces mêmes fichiers doivent être uploadés une fois manuellement
> dans le bucket R2 au même chemin (`bank/mariage.jpg`, etc.).

## Variables d'environnement

| Variable | Défaut | Description |
|---|---|---|
| `PORT` | `5029` | Port d'écoute |
| `STORAGE_DIR` | `./storage` | Répertoire de stockage des fichiers |

## Développement local

```bash
pnpm install
pnpm dev        # tsx watch — rechargement automatique
```

## Stack

- **Express** — routeur HTTP, PUT/DELETE/GET + streaming fichiers (`sendFile`)
- **node:fs** — lecture/écriture/suppression des fichiers sur disque
- **node:path** — construction et validation des chemins (protection path traversal)
- **tsx** — exécution TypeScript directe
- **Docker** — déployé via `dev/docker-compose.yml` sur le port 5029
