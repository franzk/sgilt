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
COPY ./VERSION /tmp/VERSION

# Realm (init mode only)
COPY realm/realm-import.json /opt/keycloak/data/import/

# Injecte VERSION dans l'URL du CSS (styles.css?v=...) : l'URL de thème Keycloak ne change
# qu'avec la version de Keycloak elle-même, jamais avec nos changements de thème —
# --> sans ce cache-busting, navigateurs/CDN continuent de servir l'ancien CSS après un déploiement.
RUN sed -i "s/__THEME_VERSION__/$(cat /tmp/VERSION)/" /opt/keycloak/themes/sgilt-theme/login/theme.properties && \
    rm /tmp/VERSION && \
    /opt/keycloak/bin/kc.sh build && \
    chown -R 1000:0 /opt/keycloak/data && \
    chmod -R g+rwX /opt/keycloak/data

USER 1000

EXPOSE 8080
