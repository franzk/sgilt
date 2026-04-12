package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour la saisie d'un mot de passe pour la création d'un compte client Sgilt
 */
public record ConfirmAccountRequest(
        @NotBlank String setPasswordToken,
        @NotBlank String password
) {}
