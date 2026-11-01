package net.franzka.sgilt.core.evenement.billetterie.commande.exception;

import java.util.UUID;

/**
 * Levée quand une commande est introuvable.
 */
public class CommandeNotFoundException extends RuntimeException {

    /**
     * Construit l'exception pour la commande concernée.
     *
     * @param commandeId l'identifiant de la commande
     */
    public CommandeNotFoundException(UUID commandeId) {
        super("Commande introuvable : " + commandeId);
    }
}
