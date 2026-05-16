package net.franzka.sgilt.core.onboarding.dto;

/**
 * DTO retourné après la confirmation de compte lors de l'onboarding.
 * Le front redirige le navigateur vers {@code loginUrl} pour établir la session SSO KC.
 */
public record ConfirmAccountResponse(String loginUrl) {}
