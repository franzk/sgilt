package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour la demande de réservation initiale dans le flux d'onboarding.
 */
public record DemandeInitialeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotNull UUID prestataireId,
        String eventType,
        String ambiance,
        String momentCle,
        String description,
        LocalDate date,
        String ville,
        String nbInvites,
        String lieu,
        String telephone,
        String prestataireMessage
) {}
