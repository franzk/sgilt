package net.franzka.sgilt.core.evenement.billetterie.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'un événement n'a pas de billetterie.
 */
public class BilletterieNotFoundException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public BilletterieNotFoundException(UUID evenementId) {
        super("Aucune billetterie pour l'événement " + evenementId);
    }
}
