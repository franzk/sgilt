package net.franzka.sgilt.core.onboarding.dto;

import net.franzka.sgilt.core.onboarding.domain.OnboardingState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Session d'onboarding en attente pour le suivi admin : un visiteur a commencé l'organisation d'un
 * événement et n'a pas encore créé son compte.
 */
public record OnboardingPendingDto(
        UUID id,
        String email,
        String eventType,
        LocalDate eventDate,
        int demandeCount,
        OnboardingState state,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
