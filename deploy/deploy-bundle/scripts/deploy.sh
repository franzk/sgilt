#!/usr/bin/env bash

#
# Deploy script for SGILT
# Usage:
#   cd deploy/deploy-bundle/
#   ./scripts/deploy.sh <init|update> <staging|production>
#

set -euo pipefail

MODE="${1:?mode required (init|update)}"
ENV="${2:?environment required (staging|production)}"

case "$MODE" in
  init|update) ;;
  *)
    echo "❌ Invalid mode: $MODE (must be 'init' or 'update')"
    exit 2
    ;;
esac

case "$ENV" in
  staging|production) ;;
  *)
    echo "❌ Invalid environment: $ENV (must be 'staging' or 'production')"
    exit 2
    ;;
esac

source "./scripts/lib.sh"

# Load .env (strip Windows CRLF if needed)
tr -d '\r' < .env > .env.tmp && mv .env.tmp .env
set -a; source .env; set +a

# Load .env.secrets if present (generated at init, never overwritten by subsequent runs)
if [[ -f .env.secrets ]]; then
  tr -d '\r' < .env.secrets > .env.secrets.tmp && mv .env.secrets.tmp .env.secrets
  set -a; source .env.secrets; set +a
fi

PROJECT="sgilt-${ENV}"

echo "🚀 Starting SGILT deployment — env: $ENV, mode: $MODE"

mapfile -t COMPOSE_FILES < <(compose_files_for "$MODE")

echo "-> Mode     : $MODE"
echo "-> Env      : $ENV"
echo "-> Project  : $PROJECT"
echo "-> Compose files:"
printf "  - %s\n" "${COMPOSE_FILES[@]}"

if [[ "$MODE" == "init" ]]; then
  # ── Phase 1 : start Keycloak only (realm import on first start) ───────────
  echo ""
  echo "── Phase 1 : Starting Keycloak..."
  docker compose -p "$PROJECT" \
    -f ./docker-compose.base.yml \
    -f ./docker-compose.keycloak.yml \
    -f ./docker-compose.overlay-init.yml \
    pull
  docker compose -p "$PROJECT" \
    -f ./docker-compose.base.yml \
    -f ./docker-compose.keycloak.yml \
    -f ./docker-compose.overlay-init.yml \
    up -d

  # ── Phase 2 : wait for Keycloak, generate and configure secrets ───────────
  echo ""
  echo "── Phase 2 : Waiting for Keycloak to be ready..."
  until docker exec "sgilt-keycloak-${ENV}" \
    /opt/keycloak/bin/kcadm.sh config credentials \
      --server http://localhost:8080 \
      --realm master \
      --user "$KC_BOOTSTRAP_ADMIN_USERNAME" \
      --password "$KC_BOOTSTRAP_ADMIN_PASSWORD" &>/dev/null; do
    sleep 5
  done
  echo "   Keycloak ready."

  echo "   Generating application secrets..."
  CONFIRMATION_TOKEN_SECRET=$(openssl rand -hex 32)
  KC_ADMIN_CLIENT_SECRET=$(openssl rand -hex 32)

  CLIENT_UUID=$(docker exec "sgilt-keycloak-${ENV}" \
    /opt/keycloak/bin/kcadm.sh get clients -r sgilt --fields id,clientId \
    | jq -r '.[] | select(.clientId=="sgilt-admin") | .id')

  docker exec "sgilt-keycloak-${ENV}" \
    /opt/keycloak/bin/kcadm.sh update "clients/${CLIENT_UUID}" \
    -r sgilt -s "secret=${KC_ADMIN_CLIENT_SECRET}"

  printf "KC_ADMIN_CLIENT_SECRET=%s\nCONFIRMATION_TOKEN_SECRET=%s\n" \
    "$KC_ADMIN_CLIENT_SECRET" "$CONFIRMATION_TOKEN_SECRET" > .env.secrets
  set -a; source .env.secrets; set +a
  echo "   .env.secrets written."

  # ── Phase 3 : start remaining services ────────────────────────────────────
  echo ""
  echo "── Phase 3 : Starting all services..."
fi

docker_compose_up "$PROJECT" "${COMPOSE_FILES[@]}"

echo "✅ Deployment complete."
