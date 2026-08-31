package net.franzka.sgilt.core.onboarding.dto;

import net.franzka.sgilt.core.onboarding.domain.OnboardingFlow;

/**
 * Corps de la réponse 410 sur un token expiré — le flow permet au front d'afficher un message
 * adapté (voir {@code /onboarding/verify}).
 *
 * @param flow le flow concerné, ou {@code null} si l'expiration ne provient pas d'une vérification
 *             de token à ce point d'entrée (ex. token set-password expiré à la confirmation finale)
 */
public record TokenExpiredResponseDto(OnboardingFlow flow) {}
