package net.franzka.sgilt.core.evenement.page.exception;

import java.util.UUID;

/**
 * Exception levée lorsque l'organisateur administre la page publique d'un événement dont le
 * back-office ne l'a pas activée.
 */
public class PagePubliqueDisabledException extends RuntimeException {

    /**
     * Construit l'exception pour l'événement concerné.
     *
     * @param evenementId l'identifiant de l'événement
     */
    public PagePubliqueDisabledException(UUID evenementId) {
        super("Page publique non activée pour l'événement " + evenementId);
    }
}
