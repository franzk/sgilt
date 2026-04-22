package net.franzka.sgilt.core.keycloak;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Client HTTP déclaratif pour l'API Admin Keycloak.
 * Instancié comme bean Spring via {@link KeycloakConfig}.
 */
@HttpExchange
public interface KeycloakAdminClient {

    /**
     * Crée un utilisateur dans le realm Keycloak via l'API Admin.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization (format {@code Bearer <token>})
     * @param user          les données de l'utilisateur à créer
     */
    @PostExchange("/admin/realms/{realm}/users")
    void createUser(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @RequestBody KeycloakUserRequest user
    );
}
