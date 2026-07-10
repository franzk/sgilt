# SGILT — Environment Setup

Step-by-step procedure to provision a new SGILT environment from scratch.
Follow this guide when setting up staging or production for the first time.

---

## Prerequisites

- A Linux VPS (Ubuntu 24.04 recommended) with root or sudo access
- A Cloudflare account managing the target domain (`sgilt.fr` or `sgilt.alsace`)
- A GitHub account with access to the `franzk/sgilt` repository
- Docker and Docker Compose installed on the server

---

## Step 0 — Harden server access 

À faire avant toute autre chose, sur un serveur tout juste provisionné.

### 0.1 Update the system

```bash
apt update && apt full-upgrade -y
reboot
```

### 0.2 Create a personal sudo user (if not already done)

```bash
adduser franz
usermod -aG sudo franz
rsync --archive --chown=franz:franz ~/.ssh /home/franz
```

Teste la connexion avec ce nouvel utilisateur dans un **second terminal** avant de continuer.

### 0.3 Disable root SSH login and password authentication

Édite `/etc/ssh/sshd_config` :

```
PermitRootLogin no
PasswordAuthentication no
```

```bash
systemctl restart ssh
```

### 0.4 Install fail2ban

```bash
apt install fail2ban -y
systemctl enable --now fail2ban
```

### 0.5 Add swap

Recommandé pour absorber les pics mémoire sur la stack JVM (core, gateway, mailer, Keycloak) :

```bash
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
```

### 0.6 Set timezone

```bash
timedatectl set-timezone Europe/Paris
```

---

## Step 1 — Prepare the server

### 1.1 Install Docker

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
```

Log out and back in for the group change to take effect.

### 1.2 Create the deploy user

```bash
sudo adduser deployer
sudo usermod -aG docker deployer
```

### 1.3 Create the deployment directory

```bash
sudo mkdir -p /home/sgilt-server
sudo chown -R deployer:deployer /home/sgilt-server
```

### 1.4 Add the SSH public key for GitHub Actions

Generate a dedicated SSH key pair (on your local machine):

```bash
ssh-keygen -t ed25519 -C "sgilt-github-actions" -f ~/.ssh/sgilt_deploy
```

Add the public key to the server:

```bash
sudo -u deployer mkdir -p /home/deployer/.ssh
sudo -u deployer nano /home/deployer/.ssh/authorized_keys
# paste the content of ~/.ssh/sgilt_deploy.pub
sudo chmod 700 /home/deployer/.ssh
sudo chmod 600 /home/deployer/.ssh/authorized_keys
```

The private key (`sgilt_deploy`) will be stored as a GitHub secret (`SSH_PRIVATE_KEY`).

### 1.5 Open firewall ports

In the Ionos panel, open the following TCP ports:

| Environment | Ports            |
|-------------|------------------|
| Staging     | 8443, 2053, 2096 |
| Production  | 443, 2053, 2096  |

---

## Step 2 — Configure Cloudflare

### 2.1 Add DNS records

In Cloudflare DNS for the target domain, add A records pointing to the server IP:

| Environment                 | Records                                 |
|-----------------------------|-----------------------------------------|
| Staging (`sgilt.fr`)        | `staging` → server IP (Proxied 🟠)      |
| Staging (`sgilt.fr`)        | `auth-staging` → server IP (Proxied 🟠) |
| Staging (`sgilt.fr`)        | `api-staging` → server IP (Proxied 🟠)  |
| Production (`sgilt.alsace`) | `@` → server IP (Proxied 🟠)            |
| Production (`sgilt.alsace`) | `auth` → server IP (Proxied 🟠)         |
| Production (`sgilt.alsace`) | `api` → server IP (Proxied 🟠)          |

### 2.2 Configure Origin Rules

In Cloudflare → Rules → Origin Rules, create the following rules:

| Name             | Match                             | Action                 |
|------------------|-----------------------------------|------------------------|
| Staging Front    | `https://staging.sgilt.fr/*`      | Rewrite port to `8443` |
| Staging Auth     | `https://auth-staging.sgilt.fr/*` | Rewrite port to `2053` |
| Staging API      | `https://api-staging.sgilt.fr/*`  | Rewrite port to `2096` |
| Production Front | `https://sgilt.alsace/*`          | Rewrite port to `443`  |
| Production Auth  | `https://auth.sgilt.alsace/*`     | Rewrite port to `2053` |
| Production API   | `https://api.sgilt.fr/*`          | Rewrite port to `2096` |

> Note: Production port 443 does not need an Origin Rule since it is the default HTTPS port.

### 2.3 Set SSL mode

In Cloudflare → SSL/TLS → Overview → Configure → **Full (strict)**.

---

## Step 3 — Generate and install SSL certificates

See [SSL.md](SSL.md) for the full procedure.

In short:
1. Generate a Cloudflare Origin Certificate for the domain in Cloudflare → SSL/TLS → Origin Server
2. Copy the certificate and key to `/etc/ssl/sgilt/` on the server
3. Set correct permissions

```bash
sudo mkdir -p /etc/ssl/sgilt
sudo nano /etc/ssl/sgilt/origin.pem   # paste certificate
sudo nano /etc/ssl/sgilt/origin.key   # paste private key
sudo chmod 644 /etc/ssl/sgilt/origin.pem
sudo chmod 644 /etc/ssl/sgilt/origin.key
sudo chown -R deployer:deployer /etc/ssl/sgilt
```

---

## Step 4 — Configure GitHub

### 4.1 Create the GitHub Environment

In the `franzk/sgilt` repository → Settings → Environments → New environment.

Name it `staging` or `production`.

### 4.2 Add Repository secrets

In Settings → Secrets and variables → Actions → Repository secrets:

| Secret           | Value                                 |
|------------------|---------------------------------------|
| `REGISTRY_TOKEN` | GitHub PAT with `read:packages` scope |

### 4.3 Add Environment secrets

| Secret                        | Description                                                                         |
|-------------------------------|-------------------------------------------------------------------------------------|
| `SSH_HOST`                    | Server IP or hostname                                                               |
| `SSH_USER`                    | e.g. `deployer`                                                                     |
| `SSH_PRIVATE_KEY`             | e.g. content of `~/.ssh/sgilt_deploy`                                               |
| `SSH_HOST_PATH`               | e.g. `/home/sgilt-server`                                                           |
| `KC_DB_PASSWORD`              | Keycloak PostgreSQL password (choose a strong password)                             |
| `KC_BOOTSTRAP_ADMIN_USERNAME` | Keycloak admin username                                                             |
| `KC_BOOTSTRAP_ADMIN_PASSWORD` | Keycloak admin password (choose a strong password)                                  |
| `CORE_DB_PASSWORD`            | sgilt-core PostgreSQL password (choose a strong password)                           |
| `MAGIC_LINK_SECRET`           | Secret HMAC-SHA256 partagé entre sgilt-core et le SPI KC magic-link (`openssl rand -hex 32`) |
| `SMTP_HOST`                   | SMTP server hostname                                                                |
| `SMTP_PORT`                   | SMTP port (usually `587`)                                                           |
| `SMTP_USERNAME`               | SMTP username                                                                       |
| `SMTP_PASSWORD`               | SMTP password                                                                       |
| `SMTP_AUTH`                   | `true`                                                                              |
| `SMTP_SSL`                    | `true`                                                                              |
| `R2_ACCESS_KEY_ID`            | Cloudflare R2 API token access key ID                                               |
| `R2_SECRET_ACCESS_KEY`        | Cloudflare R2 API token secret access key                                           |

### 4.4 Add Environment variables

| Variable             | Staging                         | Production                  |
|----------------------|---------------------------------|------------------------------|
| `APP_URL`            | `https://staging.sgilt.fr`      | `https://sgilt.alsace`      |
| `APP_DOMAIN`         | `staging.sgilt.fr`              | `sgilt.alsace`              |
| `AUTH_URL`           | `https://auth-staging.sgilt.fr` | `https://auth.sgilt.alsace` |
| `AUTH_DOMAIN`        | `auth-staging.sgilt.fr`         | `auth.sgilt.alsace`         |
| `API_URL`            | `https://api-staging.sgilt.fr`  | `https://api.sgilt.fr`      |
| `API_DOMAIN`         | `api-staging.sgilt.fr`          | `api.sgilt.fr`              |
| `NGINX_FRONT_PORT`   | `8443`                          | `443`                       |
| `NGINX_AUTH_PORT`    | `2053`                          | `2053`                      |
| `NGINX_API_PORT`     | `2096`                          | `2096`                      |
| `MAILER_FROM`        | `noreply@sgilt.fr`              | `noreply@sgilt.alsace`      |
| `R2_ENDPOINT`        | R2 S3 endpoint (shared)         | (idem)                      |
| `R2_BUCKET`          | `sgilt-media-staging`          | `sgilt-media-prod`         |
| `R2_DOCUMENTS_BUCKET`| `sgilt-documents-staging`      | `sgilt-documents-prod`     |
| `R2_DELIVERY_URL`    | `https://media-staging.sgilt.fr` | `https://media.sgilt.alsace` |

> **R2:** See [CLOUDFLARE.md](CLOUDFLARE.md) for the full setup procedure (token creation, endpoint format, bucket names).
> - R2_BUCKET and R2_DOCUMENTS_BUCKET are the names of the buckets.

---

## Step 5 — First deployment

In GitHub → Actions → **Build & Deploy** → Run workflow:

- **environment**: `staging` or `production`
- **mode**: `init`

> Init mode generates the Keycloak realm from the template and imports it on the first startup.

The workflow will:
1. Build all Docker images and push them to ghcr.io (including the KC image with the magic-link SPI baked in)
2. Generate the `.env` file from secrets and variables
3. Copy all deployment files to the server
4. Pull images and start all containers

#### How init mode works

The KC init sequence has three phases:

**Phase 1** — KC starts with `--import-realm`. In KC 26 production mode, this imports the realm into PostgreSQL and then KC exits.

**Phase 2** — The deploy script polls the KC PostgreSQL database directly (not the KC API) until the `sgilt` realm appears. Once confirmed, KC is restarted in normal server mode (without `--import-realm`) so it stays running. The script then waits for KC to be ready and retrieves the `sgilt-admin` client secret.

**Phase 3** — All remaining services start.

> **Note:** The `kc.sh import` command (nonserver profile) does **not** commit realm data to PostgreSQL in KC 26 — only `start --import-realm` (prod profile) does. This is why Phase 1 uses `start` and not `kc.sh import`.

---

## Step 6 — Verify

Once the workflow is completed, verify:

```bash
docker ps
```

All containers should be `Up`. Check:

| URL                             | Expected            |
|---------------------------------|---------------------|
| `https://staging.sgilt.fr`      | SGILT landing page  |
| `https://auth-staging.sgilt.fr` | Keycloak login page |

---

## Troubleshooting

**nginx container restarting** — check logs:
```bash
docker logs nginx-front-staging --tail 20
```

**SSL certificate not found** — verify the volume is mounted and permissions are correct:
```bash
ls -la /etc/ssl/sgilt/
docker exec nginx-front-staging ls /etc/ssl/sgilt/
```

**Bad gateway** — check that the upstream container is running:
```bash
docker logs sgilt-front-staging --tail 20
```

**KC init loop (Phase 2 never completes)** — KC may be crashing during realm import. Check KC logs:
```bash
docker logs sgilt-keycloak-staging --tail 50
```
Common causes: malformed realm JSON (wrong section for a flow, unknown field for the KC version). The realm is imported with `start --import-realm` in production mode. Only the `sgilt` realm template is imported; the master realm is always created fresh by KC.

**Realm not imported after init** — Verify the realm is in the KC database:
```bash
docker exec sgilt-keycloak-db-staging \
  psql -U sgilt-keycloak -d sgilt-keycloak \
  -c "SELECT id, name FROM realm;"
```
Both `master` and `sgilt` should appear. If only `master` is present, the import failed silently. Re-run in `init` mode after fixing the realm template.

**Cloudflare 526 error** — verify the Origin Rule has `/*` wildcard at the end of the match URL.