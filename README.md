# SGILT [WIP]

SGILT is a platform that connects event organizers with local service providers in Alsace.
Built on a Docker-first, single-server architecture with Keycloak authentication and a Nuxt SSR frontend.

---

## Documentation

### Architecture
> Stack, infrastructure overview, Cloudflare setup, Docker network isolation.

👉 [doc/ARCHITECTURE.md](doc/ARCHITECTURE.md)

### Local development
> How to run the project locally for feature development and debugging.

👉 [dev/README.md](dev/README.md)

### Development cycle
> Step-by-step workflow for developing a feature, deploying to staging, and promoting to production.

👉 [DEV_CYCLE.md](doc/DEV_CYCLE.md)

### Environment setup
> Step-by-step procedure to provision a new environment (staging or production) from scratch.

👉 [deploy/docs/SETUP.md](deploy/docs/SETUP.md)

### Deployment
> How to use the GitHub Actions workflow to build and deploy. Includes version management and rollback.

👉 [deploy/docs/DEPLOY.md](deploy/docs/DEPLOY.md)

### SSL certificates
> How to generate and install Cloudflare Origin certificates on a server.

👉 [deploy/docs/SSL.md](deploy/docs/SSL.md)

---

## Services overview

| Service         | Technology    | Description                             |
|-----------------|---------------|-----------------------------------------|
| Frontend        | Nuxt 4 (SSR)  | Server-side rendered frontend           |
| Gateway         | Spring Boot   | API Gateway (single entry point)        |
| Business        | Spring Boot   | Business logic and data                 |
| Keycloak        | Keycloak 26   | Identity & access management (OIDC)     |
| PostgreSQL      | PostgreSQL 17 | Keycloak and business databases         |
| SMTP Bridge     | Node.js       | SMTP to HTTP bridge for Keycloak emails |
| Mailer          | Spring Boot   | Email delivery service                  |
| Google Calendar | Spring Boot   | Google Calendar sync service            |

---

## Environments

| Environment | Frontend                 | Auth                          |
|-------------|--------------------------|-------------------------------|
| Staging     | https://staging.sgilt.fr | https://auth-staging.sgilt.fr |
| Production  | https://sgilt.alsace     | https://auth.sgilt.alsace     |