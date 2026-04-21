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
  kc_curl() {
    docker run --rm --network "sgilt-network-${ENV}" curlimages/curl -s "$@"
  }
  KC_BASE="http://sgilt-keycloak-${ENV}:8080"

  echo "── Phase 2 : Waiting for Keycloak realm import (including clients)..."
  KC_TOKEN=""
  CLIENT_UUID=""
  until [[ -n "$CLIENT_UUID" && "$CLIENT_UUID" != "null" ]]; do
    sleep 5
    KC_TOKEN=$(kc_curl -X POST "${KC_BASE}/realms/master/protocol/openid-connect/token" \
      -d "client_id=admin-cli&grant_type=password&username=${KC_BOOTSTRAP_ADMIN_USERNAME}&password=${KC_BOOTSTRAP_ADMIN_PASSWORD}" \
      | jq -r '.access_token // empty')
    [[ -z "$KC_TOKEN" ]] && continue
    CLIENT_UUID=$(kc_curl "${KC_BASE}/admin/realms/sgilt/clients?clientId=sgilt-admin" \
      -H "Authorization: Bearer ${KC_TOKEN}" | jq -r '.[0].id // empty')
  done
  echo "   sgilt realm and clients ready."

  echo "   Configuring application secrets..."
  CONFIRMATION_TOKEN_SECRET=$(openssl rand -hex 32)

  KC_ADMIN_CLIENT_SECRET=$(kc_curl \
    "${KC_BASE}/admin/realms/sgilt/clients/${CLIENT_UUID}/client-secret" \
    -H "Authorization: Bearer ${KC_TOKEN}" | jq -r '.value')
  [[ -z "$KC_ADMIN_CLIENT_SECRET" || "$KC_ADMIN_CLIENT_SECRET" == "null" ]] && { echo "❌ Failed to read KC_ADMIN_CLIENT_SECRET"; exit 1; }

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
