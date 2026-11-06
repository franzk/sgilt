#!/usr/bin/env bash

#
# Stripe webhooks setup for local development
# Usage (from the repository root, in an interactive WSL / Linux / macOS terminal):
#   bash dev/setup-stripe-webhook.sh
#
# Logs the Stripe CLI in if it has no valid session yet (first install, or expired after 90 days):
# open the printed URL and pick the sandbox of your sk_test_ key. The session is kept in the
# stripe_cli_config Docker volume.
# Then gets the webhook signing secret of the Stripe CLI session (whsec_…), writes it to
# sgilt.stripe.webhook-secret in sgilt-pay's local secrets file (created from the .example if
# missing), then starts the sgilt-stripe-cli container. The secret is never printed.
#
# Restart sgilt-pay afterwards so it reads the new secret.
# See dev/README.md, "SETUP payments — Stripe".
#

set -euo pipefail

cd "$(dirname "$0")"

SECRETS_FILE=../sgilt-pay/src/main/resources/application-local-secrets.yml

if [[ ! -f "$SECRETS_FILE" ]]; then
  cp "$SECRETS_FILE.example" "$SECRETS_FILE"
  echo "Created $SECRETS_FILE from the example: fill in sgilt.stripe.secret-key too."
fi

# Webhook signing secret of the current Stripe CLI session, empty without a valid session.
print_secret() {
  docker compose run --rm -T sgilt-stripe-cli listen --print-secret 2>/dev/null \
    | grep -o 'whsec_[A-Za-z0-9]*' || true
}

secret=$(print_secret)
if [[ -z "$secret" ]]; then
  echo "The Stripe CLI is not logged in: open the URL below and pick the sandbox of your sk_test_ key."
  docker compose run --rm sgilt-stripe-cli login
  secret=$(print_secret)
fi
if [[ -z "$secret" ]]; then
  echo "Still no webhook secret after login: check docker compose run --rm sgilt-stripe-cli listen --print-secret" >&2
  exit 1
fi

# Replaces the webhook-secret line, or adds it under secret-key (same indentation) for a secrets
# file created before the property existed.
if grep -q '^[[:space:]]*webhook-secret:' "$SECRETS_FILE"; then
  sed -i "s|^\([[:space:]]*webhook-secret:\).*|\1 $secret|" "$SECRETS_FILE"
else
  sed -i "s|^\([[:space:]]*\)secret-key:.*|&\n\1webhook-secret: $secret|" "$SECRETS_FILE"
fi
echo "Webhook secret written to $SECRETS_FILE."

docker compose up -d sgilt-stripe-cli
echo "sgilt-stripe-cli started"
echo
echo ">>> Restart sgilt-pay (reads the webhook secret)."
echo
echo "docker logs -f sgilt-stripe-cli must show [200] on a successful payment."