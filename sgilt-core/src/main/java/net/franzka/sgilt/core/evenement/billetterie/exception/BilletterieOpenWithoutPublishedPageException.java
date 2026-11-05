package net.franzka.sgilt.core.evenement.billetterie.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'on ouvre la billetterie d'un événement dont la page n'est pas publiée :
 * personne ne pourrait acheter.
 */
public class BilletterieOpenWithoutPublishedPageException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public BilletterieOpenWithoutPublishedPageException(UUID evenementId) {
        super("Billetterie ouverte sans page publiée pour l'événement " + evenementId);
    }
}
