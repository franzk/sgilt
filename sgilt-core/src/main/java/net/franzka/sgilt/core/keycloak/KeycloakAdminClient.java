package net.franzka.sgilt.core.keycloak;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

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

    /**
     * Supprime un utilisateur du realm Keycloak par son UUID.
     * Utilisé pour compenser une création DB échouée après un compte KC déjà créé.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization
     * @param userId        l'UUID Keycloak de l'utilisateur à supprimer
     */
    @DeleteExchange("/admin/realms/{realm}/users/{userId}")
    void deleteUser(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @PathVariable String userId
    );

    /**
     * Recherche des utilisateurs par email.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization
     * @param email         l'email recherché
     * @param exact         {@code true} pour n'accepter qu'une correspondance exacte
     * @return la liste des utilisateurs correspondants (au plus 1 normalement, email unique)
     */
    @GetExchange("/admin/realms/{realm}/users")
    List<KeycloakUserRepresentation> getUsersByEmail(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @RequestParam String email,
            @RequestParam boolean exact
    );

    /**
     * Définit un nouveau mot de passe pour un utilisateur existant.
     *
     * @param realm         le realm Keycloak cible
     * @param authorization le header Authorization
     * @param userId        l'UUID Keycloak de l'utilisateur
     * @param credential    le nouveau credential mot de passe
     */
    @PutExchange("/admin/realms/{realm}/users/{userId}/reset-password")
    void resetPassword(
            @PathVariable String realm,
            @RequestHeader("Authorization") String authorization,
            @PathVariable String userId,
            @RequestBody KeycloakCredential credential
    );
}
