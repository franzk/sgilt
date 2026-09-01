# SGILT — Architecture

## Overview

SGILT is a web application deployed on one or more Linux servers (Ionos VPS).
All incoming traffic passes through Cloudflare before reaching the server(s).

## Infrastructure diagram

```
Internet
    |
    +-- Cloudflare (SSL Full strict, cache, WAF)
            |
            +-- staging.sgilt.fr       :8443 --> nginx-front-staging  --> sgilt-front:3000
            +-- auth-staging.sgilt.fr  :2053 --> nginx-auth-staging   --> sgilt-keycloak:8080
            +-- api-staging.sgilt.fr   :2096 --> nginx-api-staging    --> sgilt-gateway
            +-- sgilt.alsace            :443 --> nginx-front-prod     --> sgilt-front:3000
            +-- auth.sgilt.alsace      :2053 --> nginx-auth-prod      --> sgilt-keycloak:8080
            +-- api.sgilt.fr           :2096 --> nginx-api-prod       --> sgilt-gateway
```

`sgilt-gateway` is the single entry point for the API — it proxies to `sgilt-core` and
`sgilt-notifications`, neither of which is reachable directly from outside the Docker network.

Object storage (media uploads, documents) is Cloudflare R2, called directly by `sgilt-core`
(S3 SDK) and served publicly through its own custom domain (`media-staging.sgilt.fr` /
`media.sgilt.alsace`) — see [CLOUDFLARE.md](../deploy/docs/CLOUDFLARE.md) for buckets, tokens and
image Transformations. In local dev, `sgilt-r2-mock` stands in for R2 (see
[dev/README.md](../dev/README.md)).

## Servers

| Environment | Server                               | Notes                  |
|-------------|--------------------------------------|-------------------------|
| Staging     | Ionos VPS (shared with ka-proxy POC) | Ports 8443, 2053, 2096 |
| Production  | Dedicated Ionos VPS                  | Ports 443, 2053, 2096  |

Each environment runs in its own isolated Docker network:
- `sgilt-network-staging`
- `sgilt-network-production`

On the staging server, ka-proxy runs in parallel on port 443 for other projects (POC).
SGILT does not use ka-proxy — each SGILT nginx container exposes its port directly on the host.

On the production server, SGILT is the only app — nginx-front can listen directly on port 443.

## Cloudflare

Cloudflare is the single entry point for all SGILT traffic.

- **SSL mode**: Full (strict)
- **Origin Rules**: route each domain to the correct port on the server
- **Origin Certificates**: `*.sgilt.fr` and `*.sgilt.alsace` installed on each server

## Tech stack

| Component          | Technology                          |
|--------------------|--------------------------------------|
| Frontend           | Nuxt 4 (SSR)                        |
| API Gateway        | Spring Boot (Spring Cloud Gateway)  |
| Core               | Spring Boot                         |
| Notifications      | Spring Boot                         |
| Auth               | Keycloak 26                         |
| Database           | PostgreSQL 17                       |
| Message broker     | RabbitMQ 4                          |
| Object storage     | Cloudflare R2 (S3-compatible)       |
| Mailer             | Spring Boot                         |
| SMTP Bridge        | Node.js                             |
| Reverse proxy      | nginx:alpine                        |
| Container registry | GitHub Container Registry (ghcr.io) |
| CI/CD              | GitHub Actions                      |

## Services per environment

| Service       | Staging container              | Production container              |
|----------------|-----------------------------------|---------------------------------------|
| Frontend Nuxt | `sgilt-front-staging`          | `sgilt-front-production`          |
| Nginx front   | `nginx-front-staging`          | `nginx-front-production`          |
| Keycloak      | `sgilt-keycloak-staging`       | `sgilt-keycloak-production`       |
| Keycloak DB   | `sgilt-keycloak-db-staging`    | `sgilt-keycloak-db-production`    |
| Nginx auth    | `nginx-auth-staging`           | `nginx-auth-production`           |
| Core          | `sgilt-core-staging`           | `sgilt-core-production`           |
| Core DB       | `sgilt-core-db-staging`        | `sgilt-core-db-production`        |
| Gateway       | `sgilt-gateway-staging`        | `sgilt-gateway-production`        |
| Nginx API     | `nginx-api-staging`            | `nginx-api-production`            |
| Notifications | `sgilt-notifications-staging`  | `sgilt-notifications-production`  |
| RabbitMQ      | `rabbitmq-staging`             | `rabbitmq-production`             |
| Mailer        | `sgilt-mailer-staging`         | `sgilt-mailer-production`         |
| SMTP Bridge   | `sgilt-smtp-bridge-staging`    | `sgilt-smtp-bridge-production`    |

## Host ports

| Environment | Service     | Host port | Cloudflare Origin Rule            |
|-------------|-------------|-----------|--------------------------------------|
| Staging     | nginx-front | 8443      | staging.sgilt.fr/* --> 8443       |
| Staging     | nginx-auth  | 2053      | auth-staging.sgilt.fr/* --> 2053  |
| Staging     | nginx-api   | 2096      | api-staging.sgilt.fr/* --> 2096   |
| Production  | nginx-front | 443       | sgilt.alsace/* --> 443            |
| Production  | nginx-auth  | 2053      | auth.sgilt.alsace/* --> 2053      |
| Production  | nginx-api   | 2096      | api.sgilt.fr/* --> 2096           |

These ports must be open in the Ionos firewall and configured as Origin Rules in Cloudflare.
See [SETUP.md](../deploy/docs/SETUP.md) for the full provisioning procedure.

## Docker Compose file structure

The deployment is split into multiple compose files that are merged at runtime:

| File                              | Contents                                                     |
|-------------------------------------|--------------------------------------------------------------------|
| `docker-compose.base.yml`         | Docker network and volumes                                   |
| `docker-compose.front.yml`        | sgilt-front + nginx-front                                    |
| `docker-compose.keycloak.yml`     | sgilt-keycloak + postgres + nginx-auth                       |
| `docker-compose.back.yml`         | sgilt-core + sgilt-core-db + sgilt-gateway + nginx-api + sgilt-notifications + RabbitMQ + sgilt-mailer + sgilt-smtp-bridge |
| `docker-compose.overlay-init.yml` | Keycloak --import-realm flag (init mode only)                |

## GitHub Actions secrets and variables

Full setup procedure (how to generate/obtain each value) is in
[SETUP.md](../deploy/docs/SETUP.md). Summary below.

### Repository secrets (shared across all environments)
| Secret           | Description                         |
|------------------|-------------------------------------|
| `REGISTRY_TOKEN` | GitHub PAT with read:packages scope |

### Environment secrets (per environment)
| Secret                        | Description                                                                  |
|--------------------------------|--------------------------------------------------------------------------------|
| `SSH_HOST`                    | Server IP or hostname                                                        |
| `SSH_USER`                    | SSH username                                                                 |
| `SSH_PRIVATE_KEY`             | SSH private key                                                              |
| `SSH_HOST_PATH`               | Deployment path on server                                                    |
| `KC_DB_PASSWORD`              | Keycloak PostgreSQL password                                                 |
| `KC_BOOTSTRAP_ADMIN_USERNAME` | Keycloak admin username                                                      |
| `KC_BOOTSTRAP_ADMIN_PASSWORD` | Keycloak admin password                                                      |
| `CORE_DB_PASSWORD`            | sgilt-core PostgreSQL password                                               |
| `MAGIC_LINK_SECRET`           | HMAC-SHA256 secret shared between sgilt-core and the Keycloak magic-link SPI |
| `OPENAI_API_KEY`              | OpenAI API key — required for AI-assisted profile generation                |
| `SMTP_HOST`                   | SMTP server hostname                                                         |
| `SMTP_PORT`                   | SMTP server port                                                             |
| `SMTP_USERNAME`               | SMTP username                                                                |
| `SMTP_PASSWORD`               | SMTP password                                                                |
| `SMTP_AUTH`                   | SMTP auth enabled (true/false)                                               |
| `SMTP_SSL`                    | SMTP SSL enabled (true/false)                                                |
| `RABBITMQ_PASSWORD`           | RabbitMQ password (user is hardcoded to `sgilt`)                            |
| `R2_ACCESS_KEY_ID`            | Cloudflare R2 API token access key ID                                        |
| `R2_SECRET_ACCESS_KEY`        | Cloudflare R2 API token secret access key                                    |

### Environment variables (per environment, non-sensitive)
| Variable              | Staging                          | Production                    |
|-------------------------|--------------------------------------|------------------------------------|
| `APP_URL`             | https://staging.sgilt.fr         | https://sgilt.alsace          |
| `APP_DOMAIN`          | staging.sgilt.fr                 | sgilt.alsace                  |
| `AUTH_URL`            | https://auth-staging.sgilt.fr    | https://auth.sgilt.alsace     |
| `AUTH_DOMAIN`         | auth-staging.sgilt.fr            | auth.sgilt.alsace             |
| `API_URL`             | https://api-staging.sgilt.fr     | https://api.sgilt.fr          |
| `API_DOMAIN`          | api-staging.sgilt.fr             | api.sgilt.fr                  |
| `NGINX_FRONT_PORT`    | 8443                              | 443                            |
| `NGINX_AUTH_PORT`     | 2053                              | 2053                           |
| `NGINX_API_PORT`      | 2096                              | 2096                           |
| `MAILER_FROM`         | noreply@sgilt.fr                 | noreply@sgilt.alsace          |
| `R2_ENDPOINT`         | R2 S3 endpoint (shared)          | (idem)                         |
| `R2_BUCKET`           | sgilt-media-staging              | sgilt-media-prod              |
| `R2_DOCUMENTS_BUCKET` | sgilt-documents-staging          | sgilt-documents-prod          |
| `R2_DELIVERY_URL`     | https://media-staging.sgilt.fr   | https://media.sgilt.alsace    |

See [CLOUDFLARE.md](../deploy/docs/CLOUDFLARE.md) for R2 bucket/token setup and image
Transformations.
