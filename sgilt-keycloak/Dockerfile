# Use official Keycloak base image
FROM quay.io/keycloak/keycloak:26.6.0

# Enable production features
ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_LOG_LEVEL=info

USER root

# Copy custom theme
COPY ./theme /opt/keycloak/themes/sgilt-theme

# Copy realm import file
COPY realm/realm-import.json /opt/keycloak/data/import/

# Build Keycloak, then fix ownership so user 1000 can write at runtime
# (kc.sh build runs as root and creates files in /opt/keycloak/data/ owned by root,
# which prevents KC from creating its gzip cache directory at serving time)
RUN /opt/keycloak/bin/kc.sh build && \
    chown -R 1000:0 /opt/keycloak/data && \
    chmod -R g+rwX /opt/keycloak/data

USER 1000

# Expose default Keycloak port
EXPOSE 8080
