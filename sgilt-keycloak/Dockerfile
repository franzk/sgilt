# ── Stage 1 : build du SPI JAR ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS spi-builder
WORKDIR /build
COPY spi/pom.xml .
COPY spi/src ./src
RUN mvn package -q -DskipTests

# ── Stage 2 : image Keycloak finale ────────────────────────────────────────
FROM quay.io/keycloak/keycloak:26.6.0

ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_LOG_LEVEL=info

USER root

# SPI magic-link
COPY --from=spi-builder /build/target/sgilt-keycloak-spi-1.0.0.jar /opt/keycloak/providers/

# Thème custom
COPY ./theme /opt/keycloak/themes/sgilt-theme

# Realm (init mode only)
COPY realm/realm-import.json /opt/keycloak/data/import/

RUN /opt/keycloak/bin/kc.sh build && \
    chown -R 1000:0 /opt/keycloak/data && \
    chmod -R g+rwX /opt/keycloak/data

USER 1000

EXPOSE 8080
