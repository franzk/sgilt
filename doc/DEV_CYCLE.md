# SGILT — Development Cycle

Typical workflow for developing and deploying a feature.

---

## Overview

```
feature branch
    |
    +-- develop locally
    |
    +-- bump VERSION in affected services
    +-- update deploy/staging.env with new version
    +-- commit + push
    |
    +-- GitHub Actions → Run workflow → staging → update
    |
    +-- test on staging
    +-- fix if needed → repeat
    |
    +-- ok → update deploy/production.env with validated version
    +-- merge to main
    |
    +-- GitHub Actions → Run workflow → production → update
```

---

## Step by step

### 1. Create a feature branch

```bash
git checkout -b feature/my-feature
```

### 2. Develop locally

Run the stack locally using Docker Compose.
See [dev/README.md](../dev/README.md) for details.

### 3. Bump the VERSION of affected services

Each service has a `VERSION` file at its root.
The build workflow skips building an image if the tag already exists in ghcr.io.
Bumping the version forces a new build.

```
sgilt-front/VERSION        e.g. 1.2.0
sgilt-keycloak/VERSION     e.g. 1.0.1
sgilt-mailer/VERSION       e.g. 1.1.0
sgilt-smtp-bridge/VERSION  e.g. 1.0.0
```

Use [semantic versioning](https://semver.org/):
- `PATCH` (1.0.x) — bug fix
- `MINOR` (1.x.0) — new feature, backward compatible
- `MAJOR` (x.0.0) — breaking change

### 4. Update staging.env

Edit `deploy/staging.env` to reference the new version(s):

```bash
SGILT_FRONT_VERSION=1.2.0
```

### 5. Commit and push

```bash
git add .
git commit -m "feat: my feature description"
git push origin feature/my-feature
```

### 6. Deploy to staging

In GitHub → Actions → **Build & Deploy** → Run workflow:
- Branch: `feature/my-feature`
- Environment: `staging`
- Mode: `update`

### 7. Test on staging

Verify the feature works as expected on `https://staging.sgilt.fr`.

### 8. Fix and iterate

If fixes are needed, repeat steps 2–7 with a new patch version bump.

### 9. Promote to production

Once validated on staging:

1. Update `deploy/production.env` with the validated version
2. Merge the feature branch to `main`
3. In GitHub → Actions → **Build & Deploy** → Run workflow:
    - Branch: `main`
    - Environment: `production`
    - Mode: `update`

> Production always deploys from `main`.
> `main` always reflects what is running in production.