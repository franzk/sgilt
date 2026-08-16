# SGILT — Backups

Sauvegarde et restauration des bases PostgreSQL (`sgilt-core` et `sgilt-keycloak`).

---

## Ce qui est sauvegardé

| Donnée                          | Sauvegardé ? | Pourquoi                                                                     |
|----------------------------------|--------------|-------------------------------------------------------------------------------|
| DB `sgilt-core` (métier)         | ✅            | Donnée non reconstructible                                                    |
| DB `sgilt-keycloak` (auth)       | ✅            | Donnée non reconstructible                                                    |
| Fichiers R2 (media, documents)   | ❌            | Déjà externalisés sur Cloudflare R2, hors du serveur                          |
| RabbitMQ                        | ❌            | Données de queue éphémères, reconstruites au redémarrage des services         |
| `.env.secrets`                  | ❌            | Régénérable via `deploy.sh init` (voir [DEPLOY.md](DEPLOY.md))                |

## Où vont les backups

- **Local** : `/home/sgilt-server/backups/<env>/`, conservés `RETENTION_DAYS` jours (7 par défaut).
- **Offsite** : poussés vers le bucket R2 privé `R2_DOCUMENTS_BUCKET`, sous le préfixe `backup/core/` et `backup/keycloak/`.
  Ce bucket est déjà privé (pas de custom domain) — voir [CLOUDFLARE.md](CLOUDFLARE.md). Aucun nouveau bucket ni credential n'est nécessaire, le script réutilise `R2_ACCESS_KEY_ID` / `R2_SECRET_ACCESS_KEY` / `R2_ENDPOINT` déjà présents dans `.env`.

> **Rétention offsite :** configurer une règle de lifecycle sur `R2_DOCUMENTS_BUCKET` pour purger `backup/*` après un certain délai (ex. 30-90 jours) : Dashboard → R2 → bucket → **Settings** → **Object lifecycle rules** → règle scopée au préfixe `backup/`. Pas de suppression distante scriptée côté `backup.sh`.

---

## Mise en place

Rien à faire manuellement : `deploy.sh` installe (ou met à jour) l'entrée cron à chaque déploiement, via `install_backup_cron` dans `lib.sh`. C'est idempotent — un marqueur (`# sgilt-backup-<env>`) identifie la ligne gérée et la remplace plutôt que d'en ajouter une nouvelle à chaque run, donc les crontabs manuels existants ne sont pas dupliqués ni écrasés.

- Production : tous les jours à 3h.
- Staging : tous les jours à 4h (décalé pour éviter que les deux tournent en même temps si les environnements partagent un serveur).

Vérifier l'installation sur le serveur :

```bash
crontab -l
```

### Test manuel

```bash
cd /home/sgilt-server
./scripts/backup.sh production
```

Vérifier la sortie et le contenu de `/home/sgilt-server/backups/production/`.

### Tester en local

Les noms de conteneur/utilisateur/DB suivent la convention prod par défaut (`sgilt-core-db-<env>`, user/db `sgilt-core`, etc.), mais sont overridables — utile pour tester le script contre le stack de dev (`dev/docker-compose.yml`) sans rien modifier :

```bash
cd deploy/deploy-bundle
CORE_DB_CONTAINER=sgilt-db CORE_DB_USER=sgilt CORE_DB_NAME=sgilt \
KEYCLOAK_DB_CONTAINER=sgilt-keycloak-db KEYCLOAK_DB_USER=keycloak KEYCLOAK_DB_NAME=keycloak \
BACKUP_ROOT=/tmp/sgilt-backups \
./scripts/backup.sh local
```

`.env` est chargé seulement s'il existe (sinon la partie push R2 est simplement ignorée) — pas besoin d'un `.env` de déploiement pour ce test.

---

## Restauration

### 1. Récupérer le dump

Depuis le serveur (backup local) :
```bash
ls /home/sgilt-server/backups/<env>/
```

Depuis R2 (si le backup local a été perdu) :
```bash
docker run --rm \
  -e AWS_ACCESS_KEY_ID=<R2_ACCESS_KEY_ID> \
  -e AWS_SECRET_ACCESS_KEY=<R2_SECRET_ACCESS_KEY> \
  -v "$(pwd):/out" \
  amazon/aws-cli s3 cp "s3://<R2_DOCUMENTS_BUCKET>/backup/core/sgilt-core-<timestamp>.sql.gz" /out/ \
  --endpoint-url <R2_ENDPOINT>
```

### 2. Restaurer dans le conteneur

⚠️ Ceci écrase la base cible. Arrêter les services applicatifs qui écrivent dessus avant de restaurer (`sgilt-core`, `sgilt-notifications`, ou `sgilt-keycloak` selon la base concernée).

```bash
# sgilt-core
gunzip -c sgilt-core-<timestamp>.sql.gz | \
  docker exec -i sgilt-core-db-<env> psql -U sgilt-core -d sgilt-core

# sgilt-keycloak
gunzip -c sgilt-keycloak-<timestamp>.sql.gz | \
  docker exec -i sgilt-keycloak-db-<env> psql -U sgilt-keycloak -d sgilt-keycloak
```

### 3. Redémarrer les services

```bash
docker compose -p sgilt-<env> -f docker-compose.base.yml -f docker-compose.keycloak.yml -f docker-compose.back.yml -f docker-compose.front.yml restart
```

> **Serveur recréé de zéro (disaster recovery) :** relancer `deploy.sh init` (voir [SETUP.md](SETUP.md)) pour recréer l'infra et le realm Keycloak vierge, *puis* restaurer les dumps par-dessus en suivant la procédure ci-dessus. `CORE_DB_PASSWORD` reste disponible dans les secrets GitHub, ce qui permet de restaurer un dump existant sur une base fraîchement recréée.
