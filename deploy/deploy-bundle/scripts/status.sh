#!/usr/bin/env bash

#
# Status check for SGILT services
# Usage:
#   cd deploy/deploy-bundle/
#   ./scripts/status.sh <staging|production>
#
# Checks:
#   - every expected container exists and is running (restart count reported)
#   - Docker healthcheck is "healthy" where one is defined (databases, RabbitMQ)
#   - Keycloak /health/ready reports UP
#
# Also prints the JDK version of each Java container (informational, not checked).
#
# Exit code: 0 if every check passes, 1 otherwise.
#

set -euo pipefail

ENV="${1:?environment required (staging|production)}"

case "$ENV" in
  staging|production) ;;
  *)
    echo "❌ Invalid environment: $ENV (must be 'staging' or 'production')"
    exit 2
    ;;
esac

KEYCLOAK_MANAGEMENT_PORT=9000

CONTAINERS=(
  "sgilt-keycloak-db-${ENV}"
  "sgilt-keycloak-${ENV}"
  "nginx-auth-${ENV}"
  "sgilt-core-db-${ENV}"
  "rabbitmq-${ENV}"
  "sgilt-core-${ENV}"
  "sgilt-gateway-${ENV}"
  "nginx-api-${ENV}"
  "sgilt-mailer-${ENV}"
  "sgilt-smtp-bridge-${ENV}"
  "sgilt-notifications-${ENV}"
  "sgilt-pay-${ENV}"
  "sgilt-front-${ENV}"
  "nginx-front-${ENV}"
)

JAVA_CONTAINERS=(
  "sgilt-keycloak-${ENV}"
  "sgilt-core-${ENV}"
  "sgilt-gateway-${ENV}"
  "sgilt-mailer-${ENV}"
  "sgilt-notifications-${ENV}"
  "sgilt-pay-${ENV}"
)

KEYCLOAK_CONTAINER="sgilt-keycloak-${ENV}"

FAILURES=0

ok()   { echo "  ✅ $*"; }
warn() { echo "  ⚠️  $*"; }
ko()   { echo "  ❌ $*"; FAILURES=$((FAILURES + 1)); }
info() { echo "  ℹ️  $*"; }

# java_version <container> : prints the JDK version (e.g. 25.0.5.1), empty if unavailable
java_version() {
  docker exec "$1" java -version 2>&1 | head -n1 | sed -n 's/.*version "\([^"]*\)".*/\1/p' || true
}

is_running() {
  [[ "$(docker inspect --format '{{.State.Status}}' "$1" 2>/dev/null)" == "running" ]]
}

echo "🔎 SGILT status — env: $ENV"

# ── Containers ──────────────────────────────────────────────────────────────
echo ""
echo "── Containers"
for c in "${CONTAINERS[@]}"; do
  if ! docker inspect "$c" >/dev/null 2>&1; then
    ko "$c : missing"
    continue
  fi

  IFS='|' read -r status restarts health started <<< \
    "$(docker inspect --format '{{.State.Status}}|{{.RestartCount}}|{{if .State.Health}}{{.State.Health.Status}}{{end}}|{{.State.StartedAt}}' "$c")"

  details="started ${started%.*}Z"
  [[ "$restarts" != "0" ]] && details="${details}, ${restarts} restart(s)"

  if [[ "$status" != "running" ]]; then
    ko "$c : $status ($details)"
  elif [[ -n "$health" && "$health" != "healthy" ]]; then
    ko "$c : running but $health ($details)"
  elif [[ "$restarts" != "0" ]]; then
    warn "$c : running${health:+, $health} ($details)"
  else
    ok "$c : running${health:+, $health} ($details)"
  fi
done

# ── Keycloak readiness ──────────────────────────────────────────────────────
echo ""
echo "── Keycloak readiness"
if is_running "$KEYCLOAK_CONTAINER"; then
  # The Keycloak image has no curl/wget: plain HTTP over bash /dev/tcp on the management port.
  ready_response="$(docker exec "$KEYCLOAK_CONTAINER" bash -c \
    "exec 3<>/dev/tcp/localhost/${KEYCLOAK_MANAGEMENT_PORT}; printf 'GET /health/ready HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n' >&3; cat <&3" 2>/dev/null || true)"
  if grep -q '"status": "UP"' <<< "$ready_response"; then
    ok "/health/ready : UP"
  else
    ko "/health/ready : not UP"
  fi
else
  ko "$KEYCLOAK_CONTAINER not running — readiness not checked"
fi

# ── Java runtimes (informational) ───────────────────────────────────────────
echo ""
echo "── Java runtimes"
for c in "${JAVA_CONTAINERS[@]}"; do
  if is_running "$c"; then
    info "$c : JDK $(java_version "$c" | grep . || echo unknown)"
  else
    info "$c : not running"
  fi
done

# ── Summary ─────────────────────────────────────────────────────────────────
echo ""
if [[ "$FAILURES" -eq 0 ]]; then
  echo "✅ All checks passed."
else
  echo "❌ $FAILURES check(s) failed."
  exit 1
fi
