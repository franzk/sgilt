package net.franzka.sgilt.core.evenement.page.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'on change le slug d'une page déjà publiée : les liens partagés ne doivent
 * pas casser.
 */
public class PageSlugLockedException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public PageSlugLockedException(UUID evenementId) {
        super("Slug verrouillé (page déjà publiée) pour l'événement " + evenementId);
    }
}
