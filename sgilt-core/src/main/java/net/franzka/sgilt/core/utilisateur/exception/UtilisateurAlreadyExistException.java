package net.franzka.sgilt.core.utilisateur.exception;

/**
 * Exception levée lorsqu'un utilisateur avec le même email existe déjà en base.
 */
public class UtilisateurAlreadyExistException extends RuntimeException {

    /**
     * Construit l'exception avec un message par défaut.
     */
    public UtilisateurAlreadyExistException() {
        super("Un utilisateur avec cet email existe déjà.");
    }
}
