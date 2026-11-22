# Stage 1: Build
FROM gradle:9.2.1-jdk25-alpine AS build
WORKDIR /app

COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test

# ==========================
# Stage 2: Runtime
# On utilise directement l'image officielle Temurin (Alpine pour la légèreté)
FROM eclipse-temurin:25.0.5.1_2-jdk-alpine
WORKDIR /app

# On installe curl ici uniquement si c'est nécessaire (ex: pour un healthcheck)
RUN apk add --no-cache curl

# On récupère le JAR du stage précédent
COPY --from=build /app/build/libs/*.jar ./app.jar

# Utilisateur non privilégié (pas de root dans le conteneur)
RUN addgroup -S sgilt && adduser -S -G sgilt sgilt
USER sgilt

EXPOSE 5031

ENTRYPOINT ["java", "-jar", "app.jar"]
