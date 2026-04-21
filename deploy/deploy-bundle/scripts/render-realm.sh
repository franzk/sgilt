#!/usr/bin/env bash

#
# Render Keycloak realm-import.json from realm-template.<env>.json
# by replacing https://app.change.me with APP_URL
#

set -euo pipefail

ENV="${1:?Environment required (staging|production)}"

if ! command -v perl >/dev/null 2>&1; then
  echo "❌ perl is required but not installed."
  exit 1
fi

SOURCE="sgilt-keycloak/realm-template.${ENV}.json"
TARGET="sgilt-keycloak/realm/realm-import.json"
PLACEHOLDER="https://app.change.me"

: "${APP_URL:?APP_URL must be set}"
: "${KC_ADMIN_CLIENT_SECRET:?KC_ADMIN_CLIENT_SECRET must be set}"

SECRET_PLACEHOLDER="__KC_ADMIN_CLIENT_SECRET__"

if [[ ! -f "$SOURCE" ]]; then
  echo "❌ Realm template not found: $SOURCE"
  exit 1
fi

if ! perl -0777 -ne 'exit(index($_, "https://app.change.me") >= 0 ? 0 : 1)' "$SOURCE"; then
  echo "❌ Placeholder '$PLACEHOLDER' not found in $SOURCE"
  exit 1
fi

if ! perl -0777 -ne 'exit(index($_, "__KC_ADMIN_CLIENT_SECRET__") >= 0 ? 0 : 1)' "$SOURCE"; then
  echo "❌ Placeholder '$SECRET_PLACEHOLDER' not found in $SOURCE"
  exit 1
fi

perl -pe "s|\Q$PLACEHOLDER\E|$APP_URL|g; s|\Q$SECRET_PLACEHOLDER\E|$KC_ADMIN_CLIENT_SECRET|g" "$SOURCE" > "$TARGET"

echo "✅ Realm import generated"
echo "   Source : $SOURCE"
echo "   Target : $TARGET"
echo "   Replace: $PLACEHOLDER → $APP_URL"
echo "   Replace: $SECRET_PLACEHOLDER → [KC_ADMIN_CLIENT_SECRET]"