package net.franzka.sgilt.core.onboarding.dto;

public record ConfirmAccountResponse(
        String accessToken,
        String refreshToken
) {}
