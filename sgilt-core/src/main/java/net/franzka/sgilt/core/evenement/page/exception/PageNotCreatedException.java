package net.franzka.sgilt.core.evenement.page.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'on publie ou dépublie la page d'un événement que l'organisateur n'a pas
 * encore créée.
 */
public class PageNotCreatedException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public PageNotCreatedException(UUID evenementId) {
        super("Page pas encore créée pour l'événement " + evenementId);
    }
}
