# Local development

Local development is intended for feature development and debugging.
Services can be run independently, depending on what you are working on.

## Infrastructure services

Keycloak, PostgreSQL (sgilt-core + sgilt-notifications), MailHog, SMTP bridge, RabbitMQ, R2 mock,
Stripe CLI (webhook forwarding):

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

Then fill in the values:

| Property                             | Where to find it                                                       |
|--------------------------------------|------------------------------------------------------------------------|
| `sgilt.jwt.confirmation-secret`      | Any random string — generate with `openssl rand -base64 64`            |
| `sgilt.keycloak.admin-client-secret` | `dev-admin-secret` (hardcoded in the dev realm)                        |
| `sgilt.keycloak.magic-link-secret`   | `dev-magic-secret-change-in-prod` (default value in `application.yml`) |
| `sgilt.openai.api-key`               | OpenAI API key (`OPENAI_API_KEY`) — no default, required for the fiche generation IA feature |

The file is gitignored. Without it, the service starts but JWT signing, Keycloak admin calls and IA fiche generation will fail.

### SETUP payments — Stripe (sgilt-pay, Stripe CLI, frontend)

Payments go through `sgilt-pay` (port 5032), the only service talking to Stripe. Locally, everything
uses a Stripe **test sandbox**: keys start with `sk_test_` / `pk_test_`, no real money is involved.
Keys come from the Stripe dashboard (test mode) → Developers → API keys. Never commit them, never
paste them in a chat.

| Where                                                | Property / variable                  | Value                                                  |
|------------------------------------------------------|--------------------------------------|--------------------------------------------------------|
| `sgilt-pay/src/main/resources/application-local-secrets.yml` | `sgilt.stripe.secret-key`    | Secret key (`sk_test_…`)                               |
| `sgilt-pay/src/main/resources/application-local-secrets.yml` | `sgilt.stripe.webhook-secret` | Webhook signing secret (`whsec_…`), see step 2 below  |
| `sgilt-front/.env`                                   | `NUXT_PUBLIC_STRIPE_PUBLISHABLE_KEY` | Publishable key (`pk_test_…`)                          |

Both files are gitignored. The Stripe CLI needs no key: it uses a `stripe login` session.

1. Create the `sgilt-pay` secrets file:

   ```bash
   cp sgilt-pay/src/main/resources/application-local-secrets.yml.example \
      sgilt-pay/src/main/resources/application-local-secrets.yml
   ```

2. Run the setup script from the repository root, in an interactive WSL / Linux / macOS terminal
   (on Windows, not Git Bash: its terminal is not a TTY for `docker compose run`), then restart
   `sgilt-pay`:

   ```bash
   bash dev/setup-stripe-webhook.sh
   ```

   - If the Stripe CLI has no session yet, the script logs it in: it prints a pairing code and a URL,
     open it, check that the code matches and pick the **same sandbox** as your `sk_test_` key. The
     session is kept in the `stripe_cli_config` Docker volume.
   - It writes the webhook signing secret (`whsec_…`) to `sgilt.stripe.webhook-secret`, without
     printing it, and starts the `sgilt-stripe-cli` container.

   The container listens to the sandbox events and forwards them to the gateway
   (`host.docker.internal:5080/webhooks/stripe`), which routes them to `sgilt-pay`.

   Until the script has run, `sgilt-stripe-cli` stops with an authentication error at
   `docker compose up`: harmless, the other services are not affected.

   If the CLI session token expires while `listen` is running, it exits with an authentication
   error; Docker restarts it (`restart: on-failure`) and the CLI renews the session at startup. An
   event emitted during the restart is not forwarded: resend it with
   `docker exec sgilt-stripe-cli stripe events resend evt_…`. If the container keeps restarting
   with an authentication error, the session itself is gone: run the script again.

3. Add the publishable key to `sgilt-front/.env`, then restart `pnpm dev` (Nuxt only reads `.env` at
   startup).

Run `sgilt-pay` with the `local` profile, like the other services. `sgilt-core` reaches it at
`http://localhost:5032` by default (`PAY_URL`).

Check it: `docker logs -f sgilt-stripe-cli` shows the forwarded events. A test payment (card
`4242 4242 4242 4242`, any future date, any CVC) must show `[200]` on `payment_intent.succeeded`. More
test cards and the whole payment flow: `doc/BILLETTERIE_PAIEMENT_FLOW.md`.

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
