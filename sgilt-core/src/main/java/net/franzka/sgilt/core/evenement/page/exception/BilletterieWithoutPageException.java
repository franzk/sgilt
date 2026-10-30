package net.franzka.sgilt.core.evenement.page.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'on donne une billetterie à un événement sans page : c'est sur sa page que
 * se vendent les billets.
 */
public class BilletterieWithoutPageException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public BilletterieWithoutPageException(UUID evenementId) {
        super("Billetterie sans page pour l'événement " + evenementId);
    }
}
