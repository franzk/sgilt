# SGILT — Deployment

Day-to-day deployment workflow using GitHub Actions.

> Prerequisites: the environment must already be set up.
> If not, follow [SETUP.md](SETUP.md) first.

---

## Overview

SGILT uses a single GitHub Actions workflow (`deploy.yml`) for both building and deploying.
It is triggered manually from the GitHub Actions tab.

```
GitHub Actions → build images → push to ghcr.io → deploy to server
```

---

## Workflow inputs

| Input | Options | Description |
|---|---|---|
| `environment` | `staging` / `production` | Target environment |
| `mode` | `init` / `update` | Deployment mode |

### Init mode vs Update mode

| | Init | Update |
|---|---|---|
| Build images | Yes (forces Keycloak rebuild with realm) | Yes (skips if image already exists) |
| Generate .env | Yes | Yes |
| Deploy | Yes | Yes |
| Import Keycloak realm | Yes (`--import-realm`) | No |

Use **init** only for the first deployment of an environment, or when resetting Keycloak.
Use **update** for all subsequent deployments.

---

## Version management

### How it works

Each environment has its own `.env` file that defines **exactly which image version is running**:

```
deploy/staging.env       <- what runs on staging
deploy/production.env    <- what runs on production
```

These files are the source of truth for deployments. Staging and production are completely
independent — changing the version in `staging.env` has no effect on production, and vice versa.

The docker-compose files use these variables to pull the correct image:

```yaml
image: ghcr.io/franzk/sgilt/sgilt-front:${SGILT_FRONT_VERSION}
```

### Practical example

You are working on a new feature for the frontend. Here is what the files look like at each stage:

**Initial state — both envs on 1.1.0:**
```bash
# deploy/staging.env
SGILT_FRONT_VERSION=1.1.0

# deploy/production.env
SGILT_FRONT_VERSION=1.1.0
```

**Step 1 — bump the VERSION file to build a new image:**
```bash
# sgilt-front/VERSION
1.2.0
```

**Step 2 — deploy 1.2.0 to staging only:**
```bash
# deploy/staging.env
SGILT_FRONT_VERSION=1.2.0   <- updated

# deploy/production.env
SGILT_FRONT_VERSION=1.1.0   <- unchanged, prod still on 1.1.0
```

Trigger the workflow → staging → update.
Test on https://staging.sgilt.fr.

**Step 3 — once validated, promote to production:**
```bash
# deploy/staging.env
SGILT_FRONT_VERSION=1.2.0

# deploy/production.env
SGILT_FRONT_VERSION=1.2.0   <- updated
```

Merge to main, trigger the workflow → production → update.

---

## How to deploy

### 1. Bump the VERSION file (if building a new image)

Each service has a `VERSION` file at its root:

```
sgilt-front/VERSION
sgilt-keycloak/VERSION
sgilt-mailer/VERSION
sgilt-smtp-bridge/VERSION
```

The workflow skips the build if the image tag already exists in ghcr.io.
Bumping the version forces a new build.

### 2. Update the target environment .env file

Edit `deploy/staging.env` or `deploy/production.env` to set the version you want to deploy.
Commit and push before triggering the workflow.

### 3. Trigger the workflow

In GitHub → Actions → **Build & Deploy** → **Run workflow**:

1. Select the branch to deploy from
2. Choose the target environment (`staging` or `production`)
3. Choose the mode (`init` or `update`)
4. Click **Run workflow**

---

## Image naming convention

Images are tagged with the environment prefix for Keycloak (which embeds the realm):

```
ghcr.io/franzk/sgilt/sgilt-front:1.2.0
ghcr.io/franzk/sgilt/sgilt-keycloak:staging-1.0.0
ghcr.io/franzk/sgilt/sgilt-keycloak:production-1.0.0
ghcr.io/franzk/sgilt/sgilt-mailer:1.1.0
ghcr.io/franzk/sgilt/sgilt-smtp-bridge:1.0.0
```

All other images share the same tag across environments.

---

## Checking deployment status

On the server:

```bash
# Check all containers are running
docker ps

# Check logs for a specific container
docker logs sgilt-front-staging --tail 50
docker logs sgilt-keycloak-staging --tail 50
docker logs nginx-front-staging --tail 50
```

---

## Rolling back

To roll back to a previous version:

1. Edit `deploy/staging.env` (or `production.env`) and set the previous version number
2. Commit and push
3. Trigger the workflow in **update** mode

The workflow will pull the previous image from ghcr.io and restart the containers.