package net.franzka.sgilt.core.onboarding.dto;

public record VerifyTokenResponse(
        String email,
        String setPasswordToken
) {}
