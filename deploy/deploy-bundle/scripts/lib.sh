#!/usr/bin/env bash

#
# Common functions for SGILT deployment scripts
#

set -euo pipefail

#
# Determine which docker-compose files to use based on mode
# Arguments:
#   mode: init|update
#
compose_files_for() {
  local mode="${1:?mode required}"

  echo "./docker-compose.base.yml"
  echo "./docker-compose.front.yml"
  echo "./docker-compose.keycloak.yml"
  echo "./docker-compose.mailer.yml"

  if [[ "$mode" == "init" ]]; then
    echo "./docker-compose.overlay-init.yml"
  fi
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