package net.franzka.sgilt.core.evenement.billetterie.commande.exception;

import java.util.UUID;

/**
 * Levée quand une commande n'est pas payée alors qu'elle devrait l'être (son e-billet n'existe
 * qu'une fois payée).
 */
public class CommandeNotPaidException extends RuntimeException {

    /**
     * Construit l'exception pour la commande concernée.
     *
     * @param commandeId l'identifiant de la commande
     */
    public CommandeNotPaidException(UUID commandeId) {
        super("Commande pas payée : " + commandeId);
    }
}
