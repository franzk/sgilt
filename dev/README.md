# Local development

Local development is intended for feature development and debugging.
Services can be run independently, depending on what you are working on.

## Infrastructure services

Keycloak, PostgreSQL, MailHog, SMTP bridge:

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
| `sgilt.keycloak.admin-client-secret` | Keycloak admin → Clients → `sgilt-admin` → Credentials → Client secret |

The file is gitignored. Without it, the service starts but JWT signing and Keycloak admin calls will fail.
