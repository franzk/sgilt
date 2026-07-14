package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration des tokens JWT de confirmation.
 * {@code confirmationExpirationHours} régit le tunnel de demande initiale (flux client),
 * {@code prestataireOnboardingExpirationHours} régit le lien d'onboarding prestataire — les deux
 * durées sont volontairement distinctes bien que le secret de signature HMAC soit partagé.
 */
@ConfigurationProperties(prefix = "sgilt.jwt")
public record ConfirmationTokenProperties(
        String confirmationSecret,
        int confirmationExpirationHours,
        int prestataireOnboardingExpirationHours
) {}
