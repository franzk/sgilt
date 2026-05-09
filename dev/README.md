# Local development

Local development is intended for feature development and debugging.
Services can be run independently, depending on what you are working on.

## Infrastructure services

Keycloak, PostgreSQL, MailHog, SMTP bridge:

### First time: build the Keycloak SPI

The magic-link SPI must be compiled before starting Keycloak. If Maven is not installed locally, use Docker:

```bash
docker run --rm \
  -v "${PWD}/sgilt-keycloak/spi:/build" \
  -w /build \
  maven:3.9-eclipse-temurin-21-alpine \
  mvn package -q -DskipTests
```

The JAR is output to `sgilt-keycloak/spi/target/` and automatically mounted by `docker-compose.yml`.

> Only needed again if you modify the SPI source (`sgilt-keycloak/spi/src/`).

### Start the infrastructure

```bash
cd dev
docker compose up
```

## Frontend (SPA)

```bash
cd sgilt-front
pnpm install
pnpm dev
```

## Backend services

Gateway, backend services, mailer (Spring Boot):

```bash
# From any Spring Boot service directory:
./gradlew bootRun
```

You can mix and match these commands depending on which part of the stack
you want to work on.

### Check it

Once everything is running, you can:
- access the frontend at http://localhost:3000
- access Keycloak at http://localhost:5080 (admin:admin)


### SETUP sgilt-core — local secrets

`sgilt-core` requires a secrets file that is not committed. Create it once:

```bash
cp sgilt-core/src/main/resources/application-local-secrets.yml.example \
   sgilt-core/src/main/resources/application-local-secrets.yml
```

Then fill in the two values:

| Property                             | Where to find it                                                       |
|--------------------------------------|------------------------------------------------------------------------|
| `sgilt.jwt.confirmation-secret`      | Any random string — generate with `openssl rand -base64 64`            |
| `sgilt.keycloak.admin-client-secret` | `dev-admin-secret` (hardcoded in the dev realm)                        |
| `sgilt.keycloak.magic-link-secret`   | `dev-magic-secret-change-in-prod` (default value in `application.yml`) |

The file is gitignored. Without it, the service starts but JWT signing and Keycloak admin calls will fail.
