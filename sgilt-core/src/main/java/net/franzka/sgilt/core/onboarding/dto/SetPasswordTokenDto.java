package net.franzka.sgilt.core.onboarding.dto;

/**
 * DTO contenant le token qui garantit la sécurité lors de la saisie du mot de passe dans le flux d'onboarding
 */
public record SetPasswordTokenDto(
        String email,
        String setPasswordToken
) {}
