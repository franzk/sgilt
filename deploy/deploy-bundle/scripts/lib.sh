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