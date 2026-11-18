package net.franzka.sgilt.core.admin.exception;

/**
 * Exception levée lorsque l'email d'un client à inviter correspond déjà à un compte Sgilt.
 */
public class EmailAlreadyUsedException extends RuntimeException {

    /**
     * Construit l'exception pour l'email en conflit.
     *
     * @param email l'email déjà utilisé
     */
    public EmailAlreadyUsedException(String email) {
        super("L'email '" + email + "' a déjà un compte Sgilt.");
    }
}
