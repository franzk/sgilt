package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour la création d'un événement par un utilisateur authentifié.
 * Équivalent de {@link net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest}
 * sans les champs d'identité (l'utilisateur est déjà connu via le JWT).
 */
public record CreateEventRequest(
        @NotNull UUID prestataireId,
        String eventType,
        String ambiance,
        String momentCle,
        String description,
        LocalDate date,
        String ville,
        String nbInvites,
        String lieu,
        String prestataireMessage
) {}
