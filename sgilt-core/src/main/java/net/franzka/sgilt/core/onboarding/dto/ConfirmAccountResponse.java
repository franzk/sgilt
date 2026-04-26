package net.franzka.sgilt.core.onboarding.dto;

/**
 * DTO pour renvoyer les tokens de Keycloak après création de compte par le flux d'onboarding
 */
public record ConfirmAccountResponse(
        String accessToken,
        String refreshToken
) {}
