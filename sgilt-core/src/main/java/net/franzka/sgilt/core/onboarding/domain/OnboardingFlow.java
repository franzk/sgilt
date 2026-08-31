package net.franzka.sgilt.core.onboarding.domain;

/**
 * Flow d'onboarding concerné par une vérification de token sur {@code /onboarding/verify} — le
 * client d'une demande de réservation, ou le prestataire onboardé (autonome ou clé-en-main).
 */
public enum OnboardingFlow {
    CLIENT,
    PRESTATAIRE
}
