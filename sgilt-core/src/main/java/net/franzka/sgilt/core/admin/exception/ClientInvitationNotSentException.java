package net.franzka.sgilt.core.admin.exception;

/**
 * Exception levée lorsque le mail d'invitation d'un client n'a pas pu être envoyé. Levée dans la
 * transaction de l'envoi pour l'annuler : le lien d'activation n'est ni créé ni prolongé.
 */
public class ClientInvitationNotSentException extends RuntimeException {

    /**
     * Construit l'exception pour le client dont l'invitation n'est pas partie.
     *
     * @param email l'email du client invité
     */
    public ClientInvitationNotSentException(String email) {
        super("Le mail d'invitation à '" + email + "' n'a pas pu être envoyé.");
    }
}
