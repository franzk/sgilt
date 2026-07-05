package net.franzka.sgilt.core.keycloak;

/**
 * Exception levée lorsque Keycloak refuse la création d'un utilisateur car l'email existe déjà.
 */
public class KeycloakUserAlreadyExistsException extends KeycloakException {

    /**
     * Construit l'exception pour l'email en conflit.
     *
     * @param email l'email déjà présent dans Keycloak
     * @param cause la 409 Conflict d'origine renvoyée par l'API Admin Keycloak
     */
    public KeycloakUserAlreadyExistsException(String email, Throwable cause) {
        super("Un compte Keycloak existe déjà pour l'email '" + email + "'.", cause);
    }
}
