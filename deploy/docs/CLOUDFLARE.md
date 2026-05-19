# Cloudflare Configuration

## R2 Object Storage

### Buckets

| Bucket                | Environnement | Custom Domain            |
|-----------------------|---------------|--------------------------|
| `sgilt-media-staging` | Staging       | `media-staging.sgilt.fr` |
| `sgilt-media-prod`    | Production    | `media.sgilt.alsace`     |

### Créer un bucket

Dashboard → **R2** → **Create bucket** → choisir un nom → Region : `EU`.

### Connecter un custom domain

Dashboard → **R2** → clic sur le bucket → **Settings** → **Custom Domains** → **Connect Domain** → entrer le sous-domaine. Cloudflare configure le DNS automatiquement puisque `sgilt.fr` / `sgilt.alsace` est sur le même compte.

### Créer un token API

Dashboard → **R2** → **Manage R2 API Tokens** (lien en haut à droite) → **Create API Token** :

- Permissions : **Object Read & Write**
- Scope : **All buckets** ⚠️ le scope "Specific bucket" génère un 403 malgré les apparences
- Valider → noter immédiatement **Access Key ID** et **Secret Access Key** (affichés une seule fois)

Un token par environnement.

### Variables

| Variable               | Format                                                          | Note                                     |
|------------------------|-----------------------------------------------------------------|------------------------------------------|
| `R2_ENDPOINT`          | `https://<account-id>.eu.r2.cloudflarestorage.com`              | Le `.eu.` est obligatoire pour ce compte |
| `R2_BUCKET`            | `sgilt-media-staging` / `sgilt-media-prod`                    | Différent par env                        |
| `R2_ACCESS_KEY_ID`     | Access Key ID du token                                          | Secret GitHub                            |
| `R2_SECRET_ACCESS_KEY` | Secret Access Key du token                                      | Secret GitHub                            |
| `R2_DELIVERY_URL`      | `https://media-staging.sgilt.fr` / `https://media.sgilt.alsace` | URL publique de serving                  |

L'Account ID est visible dans l'URL du dashboard : `https://dash.cloudflare.com/<account-id>/...`

### Tester les credentials

```bash
curl -X PUT "https://<account-id>.eu.r2.cloudflarestorage.com/<bucket>/test.txt" \
  --user "<ACCESS_KEY_ID>:<SECRET_ACCESS_KEY>" \
  --aws-sigv4 "aws:amz:auto:s3" \
  --data "hello"
```

Réponse attendue : HTTP 200 vide. Toute autre réponse indique un problème de credentials ou d'endpoint.

---

## Transformations

### Activer

Dashboard → **Images** → **Transformations** → sélectionner la zone `sgilt.fr` ou `sgilt.alsace` → activer.

### Configurer les sources autorisées

Sources → **Specified origins** → ajouter `media-staging.sgilt.fr` et `media.sgilt.alsace`. Empêche des tiers d'utiliser le quota de transformations sur des images externes.

### Format URL

```
https://media-staging.sgilt.fr/cdn-cgi/image/width=800,quality=80/<image-path>
```

Formats source supportés : JPEG, PNG, WebP. **AVIF et HEIC ne sont pas supportés** comme source — le back convertit automatiquement les uploads dans un format compatible.

### Cache

Les variants transformés sont mis en cache en edge par Cloudflare. Seuls les originaux sont stockés dans R2.