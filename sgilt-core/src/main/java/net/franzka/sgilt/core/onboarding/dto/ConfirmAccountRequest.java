package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmAccountRequest(
        @NotBlank String setPasswordToken,
        @NotBlank String password
) {}
