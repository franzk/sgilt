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

### Backups
> Automated PostgreSQL backups (local + offsite on R2) and restore procedure.

👉 [deploy/docs/BACKUP.md](deploy/docs/BACKUP.md)

### Cloudflare
> R2 object storage buckets, custom domains, and image Transformations.

👉 [deploy/docs/CLOUDFLARE.md](deploy/docs/CLOUDFLARE.md)

---

## Services overview

| Service      | Technology    | Description                                                    |
|--------------|---------------|------------------------------------------------------------------|
| Frontend     | Nuxt 4 (SSR)  | Server-side rendered frontend                                  |
| Gateway      | Spring Boot   | API Gateway (single entry point for Core and Notifications)    |
| Core         | Spring Boot   | Business logic and data                                        |
| Notifications| Spring Boot   | In-app + email notifications                                   |
| Keycloak     | Keycloak 26   | Identity & access management (OIDC)                            |
| PostgreSQL   | PostgreSQL 17 | Keycloak and Core databases                                    |
| RabbitMQ     | RabbitMQ 4    | Message broker (mail sending, notifications)                   |
| SMTP Bridge  | Node.js       | SMTP to HTTP bridge for Keycloak emails                        |
| Mailer       | Spring Boot   | Email delivery service                                         |
| R2 mock      | Node.js       | Local S3-compatible mock of Cloudflare R2 (dev only, not deployed) |

Object storage (uploads, images) is Cloudflare R2 directly in staging/prod — see [deploy/docs/CLOUDFLARE.md](deploy/docs/CLOUDFLARE.md).

---

## Environments

| Environment | Frontend                 | Auth                          | API                           |
|-------------|---------------------------|--------------------------------|--------------------------------|
| Staging     | https://staging.sgilt.fr | https://auth-staging.sgilt.fr | https://api-staging.sgilt.fr  |
| Production  | https://sgilt.alsace     | https://auth.sgilt.alsace     | https://api.sgilt.fr          |