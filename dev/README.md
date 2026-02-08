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
- access the frontend at http://localhost:5173
- access Keycloak at http://localhost:5080 (admin:admin)