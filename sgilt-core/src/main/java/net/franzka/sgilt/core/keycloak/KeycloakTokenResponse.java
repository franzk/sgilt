package net.franzka.sgilt.core.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO représentant la réponse du token endpoint de Keycloak.
 */
public record KeycloakTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken
) {}
