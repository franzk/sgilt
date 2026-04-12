package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO pour la demande de réservation initiale dans le flux d'onboarding.
 */
public record DemandeInitialeRequest(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank @Email String email,
        @NotNull UUID prestataireId
) {}
