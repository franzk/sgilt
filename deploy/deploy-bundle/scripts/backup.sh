#!/usr/bin/env bash

#
# Backup script for SGILT (PostgreSQL: sgilt-core + sgilt-keycloak)
# Usage:
#   cd deploy/deploy-bundle/
#   ./scripts/backup.sh <staging|production>
#
# Meant to run from cron on the server, e.g.:
#   0 3 * * * cd /home/sgilt-server && ./scripts/backup.sh production >> /home/sgilt-server/backups/backup.log 2>&1
#
# Dumps both databases, keeps a local rotating copy, and pushes them to R2.
#
# Container/user/db names follow the production convention by default
# (sgilt-core-db-<env>, user/db "sgilt-core", etc.) but can be overridden —
# e.g. to test against the local dev stack (dev/docker-compose.yml):
#   CORE_DB_CONTAINER=sgilt-db CORE_DB_USER=sgilt CORE_DB_NAME=sgilt \
#   KEYCLOAK_DB_CONTAINER=sgilt-keycloak-db KEYCLOAK_DB_USER=keycloak KEYCLOAK_DB_NAME=keycloak \
#   BACKUP_ROOT=/tmp/sgilt-backups \
#   ./scripts/backup.sh local
#
# See deploy/docs/BACKUP.md for setup and restore procedure.
#

set -euo pipefail

ENV="${1:?environment required (e.g. staging, production, local)}"

# Load .env if present (strip Windows CRLF if needed) — optional so the script
# also runs standalone (e.g. from a local checkout with no deploy .env).
if [[ -f .env ]]; then
  tr -d '\r' < .env > .env.tmp && mv .env.tmp .env
  set -a; source .env; set +a
fi

BACKUP_ROOT="${BACKUP_ROOT:-/home/sgilt-server/backups}"
BACKUP_DIR="${BACKUP_ROOT}/${ENV}"
RETENTION_DAYS="${RETENTION_DAYS:-7}"
TIMESTAMP="$(date -u +%Y%m%dT%H%M%SZ)"

CORE_DB_CONTAINER="${CORE_DB_CONTAINER:-sgilt-core-db-${ENV}}"
CORE_DB_USER="${CORE_DB_USER:-sgilt-core}"
CORE_DB_NAME="${CORE_DB_NAME:-sgilt-core}"

KEYCLOAK_DB_CONTAINER="${KEYCLOAK_DB_CONTAINER:-sgilt-keycloak-db-${ENV}}"
KEYCLOAK_DB_USER="${KEYCLOAK_DB_USER:-sgilt-keycloak}"
KEYCLOAK_DB_NAME="${KEYCLOAK_DB_NAME:-sgilt-keycloak}"

mkdir -p "$BACKUP_DIR"

echo "🗄️  Starting backup — env: $ENV, timestamp: $TIMESTAMP"

dump_db() {
  local container="${1:?}" user="${2:?}" db="${3:?}" out_file="${4:?}"

  echo "-> Dumping ${db} from ${container}..."
  docker exec "$container" pg_dump -U "$user" -d "$db" | gzip > "$out_file"

  if [[ ! -s "$out_file" ]]; then
    echo "❌ Dump is empty: $out_file"
    exit 1
  fi
  echo "   OK ($(du -h "$out_file" | cut -f1))"
}

CORE_DUMP="${BACKUP_DIR}/sgilt-core-${TIMESTAMP}.sql.gz"
KEYCLOAK_DUMP="${BACKUP_DIR}/sgilt-keycloak-${TIMESTAMP}.sql.gz"

dump_db "$CORE_DB_CONTAINER" "$CORE_DB_USER" "$CORE_DB_NAME" "$CORE_DUMP"
dump_db "$KEYCLOAK_DB_CONTAINER" "$KEYCLOAK_DB_USER" "$KEYCLOAK_DB_NAME" "$KEYCLOAK_DUMP"

echo "-> Pruning local backups older than ${RETENTION_DAYS} days..."
find "$BACKUP_DIR" -name '*.sql.gz' -mtime "+${RETENTION_DAYS}" -print -delete

if [[ -n "${R2_ACCESS_KEY_ID:-}" && -n "${R2_SECRET_ACCESS_KEY:-}" && -n "${R2_ENDPOINT:-}" && -n "${R2_DOCUMENTS_BUCKET:-}" ]]; then
  echo "-> Pushing dumps to R2 (${R2_DOCUMENTS_BUCKET}/backup/)..."

  push_to_r2() {
    local file="${1:?}" key="${2:?}"
    docker run --rm \
      -e AWS_ACCESS_KEY_ID="$R2_ACCESS_KEY_ID" \
      -e AWS_SECRET_ACCESS_KEY="$R2_SECRET_ACCESS_KEY" \
      -v "${BACKUP_DIR}:/backups:ro" \
      amazon/aws-cli s3 cp "/backups/$(basename "$file")" "s3://${R2_DOCUMENTS_BUCKET}/backup/${key}" \
      --endpoint-url "$R2_ENDPOINT"
  }

  push_to_r2 "$CORE_DUMP" "core/$(basename "$CORE_DUMP")"
  push_to_r2 "$KEYCLOAK_DUMP" "keycloak/$(basename "$KEYCLOAK_DUMP")"
else
  echo "⚠️  R2 credentials/bucket not set (R2_DOCUMENTS_BUCKET) — skipping offsite push, local copy only."
fi

echo "✅ Backup complete."