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

mapfile -t COMPOSE_FILES < <(compose_files_for)

echo "-> Mode     : $MODE"
echo "-> Env      : $ENV"
echo "-> Project  : $PROJECT"
echo "-> Compose files:"
printf "  - %s\n" "${COMPOSE_FILES[@]}"

if [[ "$MODE" == "init" ]]; then
  # ── Phase 1 : start Keycloak only (realm import on first start) ───────────
  echo ""
  echo "── Phase 1 : Starting Keycloak..."

  # Stop existing KC containers and wipe DB volume so --import-realm runs fresh
  docker compose -p "$PROJECT" \
    -f ./docker-compose.base.yml \
    -f ./docker-compose.keycloak.yml \
    down --remove-orphans 2>/dev/null || true
  docker volume rm "sgilt_keycloak_pgdata_${ENV}" 2>/dev/null || true

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

  # ── Phase 2 : wait for realm in DB, restart KC in server mode ────────────
  echo ""
  echo "── Phase 2 : Waiting for sgilt realm to be committed to database..."
  # In KC 26 production mode, start --import-realm imports the realm and exits.
  # We poll the database directly instead of waiting for KC to stay running.
  KC_DB_WAIT=0
  until docker exec "sgilt-keycloak-db-${ENV}" \
        psql -U sgilt-keycloak -d sgilt-keycloak -tAc \
        "SELECT 1 FROM realm WHERE name='sgilt'" 2>/dev/null | grep -q "1"; do
    KC_DB_WAIT=$(( KC_DB_WAIT + 1 ))
    if (( KC_DB_WAIT > 60 )); then
      echo "❌ Timeout: sgilt realm not found in database after 5 minutes"
      exit 1
    fi
    sleep 5
    echo "   Attempt ${KC_DB_WAIT}: checking database for sgilt realm..."
  done
  echo "   sgilt realm imported. Restarting Keycloak in server mode..."

  # Restart KC without the import overlay so it stays running
  docker compose -p "$PROJECT" \
    -f ./docker-compose.base.yml \
    -f ./docker-compose.keycloak.yml \
    up -d

  # Wait for KC to be ready and retrieve the admin client secret
  kc_curl() {
    docker run --rm --network "sgilt-network-${ENV}" curlimages/curl -s "$@"
  }
  KC_BASE="http://sgilt-keycloak-${ENV}:8080"

  echo "── Phase 2 : Waiting for Keycloak to be ready (including clients)..."
  KC_TOKEN=""
  CLIENT_UUID=""
  KC_WAIT_ATTEMPTS=0
  until [[ -n "$CLIENT_UUID" && "$CLIENT_UUID" != "null" ]]; do
    KC_WAIT_ATTEMPTS=$(( KC_WAIT_ATTEMPTS + 1 ))
    if (( KC_WAIT_ATTEMPTS > 60 )); then
      echo "❌ Timeout: Keycloak realm/client not ready after 5 minutes"
      exit 1
    fi
    sleep 5
    echo "   Attempt ${KC_WAIT_ATTEMPTS}: fetching token..."
    KC_TOKEN_RESP=$(kc_curl -X POST "${KC_BASE}/realms/master/protocol/openid-connect/token" \
      --data-urlencode "client_id=admin-cli" \
      --data-urlencode "grant_type=password" \
      --data-urlencode "username=${KC_BOOTSTRAP_ADMIN_USERNAME}" \
      --data-urlencode "password=${KC_BOOTSTRAP_ADMIN_PASSWORD}" 2>/dev/null) || true
    KC_TOKEN=$(echo "$KC_TOKEN_RESP" | jq -r '.access_token // empty' 2>/dev/null) || true
    if [[ -z "$KC_TOKEN" ]]; then
      KC_TOKEN_ERR=$(echo "$KC_TOKEN_RESP" | jq -r '.error_description // .error // empty' 2>/dev/null) || true
      echo "   Token: empty (${KC_TOKEN_ERR:-no response})"
      continue
    fi
    echo "   Token OK, fetching client UUID..."
    CLIENTS_RESP=$(kc_curl "${KC_BASE}/admin/realms/sgilt/clients?clientId=sgilt-admin" \
      -H "Authorization: Bearer ${KC_TOKEN}" 2>/dev/null) || true
    CLIENT_UUID=$(echo "$CLIENTS_RESP" | jq -r '.[0].id // empty' 2>/dev/null) || true
    echo "   CLIENT_UUID: ${CLIENT_UUID}"
  done
  echo "   sgilt realm and clients ready."

  echo "   Configuring application secrets..."
  CONFIRMATION_TOKEN_SECRET=$(openssl rand -hex 32)

  KC_ADMIN_CLIENT_SECRET=$(kc_curl \
    "${KC_BASE}/admin/realms/sgilt/clients/${CLIENT_UUID}/client-secret" \
    -H "Authorization: Bearer ${KC_TOKEN}" | jq -r '.value // empty')
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
