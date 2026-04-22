# syntax=docker/dockerfile:1

###############
# 1) deps
###############
FROM node:24-alpine AS deps
WORKDIR /app

# Required for some native packages (sharp, etc.)
RUN apk add --no-cache libc6-compat

# Enable corepack (pnpm)
RUN corepack enable

# Copy manifest files only (better cache)
COPY package.json pnpm-lock.yaml ./

# Install deps (prod+dev needed for build)
RUN pnpm install --frozen-lockfile

###############
# 2) build
###############
FROM node:24-alpine AS build
WORKDIR /app
RUN apk add --no-cache libc6-compat
RUN corepack enable

# Reuse node_modules from deps stage
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Build Nuxt
RUN pnpm build

###############
# 3) runner
###############
FROM node:24-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000

# Non-root user for security
RUN addgroup -S nuxt && adduser -S nuxt -G nuxt

# Copy output only
COPY --from=build /app/.output ./.output

EXPOSE 3000

USER nuxt

CMD ["node", ".output/server/index.mjs"]