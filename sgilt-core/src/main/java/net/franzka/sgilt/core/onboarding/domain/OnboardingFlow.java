package net.franzka.sgilt.core.onboarding.domain;

/**
 * Flow d'onboarding concerné par une vérification de token sur {@code /onboarding/verify} — le
 * visiteur qui crée son événement (et son compte), ou le prestataire onboardé (autonome ou clé-en-main).
 */
public enum OnboardingFlow {
    CLIENT,
    PRESTATAIRE
}
