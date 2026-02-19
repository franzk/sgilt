# syntax=docker/dockerfile:1

###############
# 1) deps
###############
FROM node:20-alpine AS deps
WORKDIR /app

# pour certains packages (sharp, etc.) selon ton projet
RUN apk add --no-cache libc6-compat

# Active corepack (pnpm)
RUN corepack enable

# Copy manifest files only (better cache)
COPY package.json pnpm-lock.yaml ./

# Install deps (prod+dev needed for build)
RUN pnpm install --frozen-lockfile

###############
# 2) build
###############
FROM node:20-alpine AS build
WORKDIR /app
RUN apk add --no-cache libc6-compat
RUN corepack enable

# Reuse node_modules from deps
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Build Nuxt
RUN pnpm build

###############
# 3) runner
###############
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000

# (optionnel) user non-root
RUN addgroup -S nuxt && adduser -S nuxt -G nuxt

# Copy output only
COPY --from=build /app/.output ./.output

# Si tu as besoin de runtime deps côté serveur (rare avec .output),
# tu peux aussi copier package.json mais généralement inutile.

EXPOSE 3000

USER nuxt

CMD ["node", ".output/server/index.mjs"]
