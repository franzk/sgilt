package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration de l'URL du frontend.
 */
@ConfigurationProperties(prefix = "sgilt.frontend")
public record FrontendProperties(
        String url
) {}
