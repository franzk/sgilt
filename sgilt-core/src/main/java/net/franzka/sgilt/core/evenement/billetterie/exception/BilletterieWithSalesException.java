package net.franzka.sgilt.core.evenement.billetterie.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'on désactive la billetterie d'un événement qui a déjà vendu des billets :
 * ses acheteurs ont des e-billets.
 */
public class BilletterieWithSalesException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public BilletterieWithSalesException(UUID evenementId) {
        super("Billetterie de l'événement " + evenementId + " déjà vendue, désactivation refusée");
    }
}
