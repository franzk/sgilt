# Local development

Local development is intended for feature development and debugging.
Services can be run independently, depending on what you are working on.

## Infrastructure services

Keycloak, PostgreSQL, MailHog, SMTP bridge, RabbitMQ, R2 mock:

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
- access the RabbitMQ management UI at http://localhost:15672 (guest:guest) — `sgilt-core` and
  `sgilt-mailer` connect to `localhost:5672` by default (`guest`:`guest`), no local secret needed.


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

---

## Keycloak SPI — magic-link authenticator

The `sgilt-keycloak/spi/` module is a custom Keycloak authenticator that enables passwordless login after account creation (onboarding flow).

### How it works

1. After creating an account via onboarding, `sgilt-core` generates a short-lived JWT signed with `MAGIC_LINK_SECRET` and returns a KC authorization URL containing a `magic_token` query parameter.
2. The browser navigates to this URL. KC routes it through the `sgilt-browser` authentication flow.
3. `MagicLinkAuthenticator` validates the JWT signature and expiry, looks up the user by email, and calls `context.success()`.
4. KC creates a real SSO session and redirects the browser to `/app/events`.
5. All subsequent logins go through the standard KC login form (username + password).

### Authentication flow: `sgilt-browser`

The realm defines a custom browser flow (`sgilt-browser`) set as the default browser flow for the realm:

```
sgilt-browser (ALTERNATIVE chain):
  ├── auth-cookie        — reuse existing KC session
  ├── magic-link         — validate one-shot magic token (this SPI)
  └── forms              — standard username + password form (fallback)
```

The `magic-link` authenticator calls `context.attempted()` (skip) when no `magic_token` is present, so normal password login works transparently.

### Shared secret

`MAGIC_LINK_SECRET` must be identical in both services:
- **sgilt-core**: signs the JWT (`sgilt.keycloak.magic-link-secret` in `application.yml`)
- **Keycloak SPI**: verifies the JWT signature (reads `MAGIC_LINK_SECRET` env var at startup)

In local dev, both default to `dev-magic-secret-change-in-prod`. In staging/production, set a strong random secret via `openssl rand -hex 32`.
