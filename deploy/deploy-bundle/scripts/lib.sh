#!/usr/bin/env bash

#
# Common functions for SGILT deployment scripts
#

set -euo pipefail

#
# List the docker-compose files for a full deployment (all services)
#
compose_files_for() {
  echo "./docker-compose.base.yml"
  echo "./docker-compose.keycloak.yml"
  echo "./docker-compose.back.yml"
  echo "./docker-compose.front.yml"
}

#
# Run docker compose up with the given compose files
# Arguments:
#   project: Docker compose project name
#   compose files...
#
docker_compose_up() {
  local project="${1:?project required}"
  shift

  local -a args=(-p "$project")

  for f in "$@"; do
    args+=(-f "$f")
  done

  echo "🚀 docker compose ${args[*]} pull"
  docker compose "${args[@]}" pull

  echo "🚀 docker compose ${args[*]} up -d"
  docker compose "${args[@]}" up -d

  docker compose "${args[@]}" ps
}

#
# Install (or refresh) the nightly backup cron entry for an environment.
# Idempotent: replaces any previous entry carrying the same marker instead of
# appending a duplicate on every deploy.
# Arguments:
#   env: staging | production
#
install_backup_cron() {
  local env="${1:?env required}"
  local dir marker hour cron_line existing

  if ! command -v crontab >/dev/null 2>&1; then
    echo "⚠️  crontab not available — skipping backup cron install."
    return 0
  fi

  dir="$(pwd)"
  marker="sgilt-backup-${env}"
  hour=3
  [[ "$env" == "staging" ]] && hour=4

  mkdir -p "${dir}/backups/${env}"

  cron_line="0 ${hour} * * * cd ${dir} && ./scripts/backup.sh ${env} >> ${dir}/backups/${env}/backup.log 2>&1 # ${marker}"

  # crontab -l exits non-zero when no crontab exists yet (first run) — tolerate that.
  existing="$(crontab -l 2>/dev/null | grep -v "# ${marker}" || true)"
  printf '%s\n%s\n' "$existing" "$cron_line" | grep -v '^$' | crontab -
  echo "🕒 Backup cron installed for ${env}: ${cron_line}"
}