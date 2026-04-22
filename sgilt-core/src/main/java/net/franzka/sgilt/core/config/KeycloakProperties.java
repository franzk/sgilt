package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration pour l'intégration Keycloak.
 */
@ConfigurationProperties(prefix = "sgilt.keycloak")
public record KeycloakProperties(
        String adminUrl,
        String realm,
        String adminClientId,
        String adminClientSecret,
        String frontClientId
) {}
