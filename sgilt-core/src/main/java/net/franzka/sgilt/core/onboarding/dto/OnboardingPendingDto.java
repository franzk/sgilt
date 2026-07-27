package net.franzka.sgilt.core.onboarding.dto;

import net.franzka.sgilt.core.onboarding.domain.OnboardingState;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO d'une session d'onboarding utilisateur (client) en attente — le lien envoyé par email n'a
 * pas encore été utilisé pour finaliser la création du compte.
 */
public record OnboardingPendingDto(
        UUID id,
        String email,
        String prestataireName,
        OnboardingState state,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
