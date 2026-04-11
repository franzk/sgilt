package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sgilt.jwt")
public record ConfirmationTokenProperties(
        String confirmationSecret,
        int confirmationExpirationHours
) {}
