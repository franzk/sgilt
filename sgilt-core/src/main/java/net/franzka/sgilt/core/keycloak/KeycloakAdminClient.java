package net.franzka.sgilt.core.keycloak;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

/**
 * Client HTTP déclaratif pour l'API Admin Keycloak.
 * Instancié comme bean Spring via {@link KeycloakConfig}.
 */
@HttpExchange
public interface KeycloakAdminClient {

    /**
     * Crée un utilisateur dans le realm Keycloak via l'API Admin.
     * Retourne un {@link ResponseEntity} pour accéder au header {@code Location}
     * contenant l'URL (et donc l'UUID) de l'utilisateur créé.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization (format {@code Bearer <token>})
     * @param user          les données de l'utilisateur à créer
     * @return la réponse 201 avec le header {@code Location}
     */
    @PostExchange("/admin/realms/{realm}/users")
    ResponseEntity<Void> createUser(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @RequestBody KeycloakUserRequest user
    );

    /**
     * Récupère un rôle realm par son nom.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization
     * @param roleName      le nom du rôle (ex : {@code USER})
     * @return la représentation du rôle (id + name)
     */
    @GetExchange("/admin/realms/{realm}/roles/{roleName}")
    KeycloakRoleRepresentation getRole(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @PathVariable String roleName
    );

    /**
     * Assigne une liste de rôles realm à un utilisateur.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization
     * @param userId        l'UUID Keycloak de l'utilisateur
     * @param roles         les rôles à assigner
     */
    @PostExchange("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    void assignRoles(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @PathVariable String userId,
            @RequestBody List<KeycloakRoleRepresentation> roles
    );
}
