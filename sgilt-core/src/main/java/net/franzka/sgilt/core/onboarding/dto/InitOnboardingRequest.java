package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour la demande de réservation initiale dans le flux d'onboarding.
 */
public record InitOnboardingRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotNull UUID prestataireId,
        @Size(max = 100) String eventType,
        @Size(max = 100) String ambiance,
        @Size(max = 100) String momentCle,
        @Size(max = 2000) String description,
        LocalDate date,
        @Size(max = 100) String ville,
        @Size(max = 20) String nbInvites,
        @Size(max = 200) String lieu,
        @Size(max = 30)
        @Pattern(regexp = "^(?:[\\s\\-.()/+]*\\d){7,15}[\\s\\-.()/+]*$", message = "Numéro de téléphone invalide")
        String telephone,
        @Size(max = 1000) String prestataireMessage
) {}
