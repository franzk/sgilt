package net.franzka.sgilt.core.keycloak;

import java.util.List;

/**
 * DTO représentant le corps de requête pour la création d'un utilisateur via l'API Admin Keycloak.
 */
record KeycloakUserRequest(
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean emailVerified,
        List<KeycloakCredential> credentials
) {}
