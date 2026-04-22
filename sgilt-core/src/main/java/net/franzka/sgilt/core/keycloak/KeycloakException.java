package net.franzka.sgilt.core.keycloak;

/**
 * Exception levée en cas d'erreur lors d'un appel à l'API Keycloak.
 */
public class KeycloakException extends RuntimeException {

    /**
     * Construit l'exception avec un message et la cause d'origine.
     *
     * @param message description de l'erreur
     * @param cause   exception d'origine
     */
    public KeycloakException(String message, Throwable cause) {
        super(message, cause);
    }
}
