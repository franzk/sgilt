package net.franzka.sgilt.core.keycloak;

/**
 * DTO représentant un credential Keycloak dans le corps de création d'utilisateur.
 */
record KeycloakCredential(
        String type,
        String value,
        boolean temporary
) {}
