package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration du token JWT de confirmation de demande initiale.
 */
@ConfigurationProperties(prefix = "sgilt.jwt")
public record ConfirmationTokenProperties(
        String confirmationSecret,
        int confirmationExpirationHours
) {}
