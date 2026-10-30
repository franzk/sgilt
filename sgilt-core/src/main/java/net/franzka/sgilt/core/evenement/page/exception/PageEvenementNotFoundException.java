package net.franzka.sgilt.core.evenement.page.exception;

/**
 * Levée quand aucune page d'événement ne correspond au slug demandé.
 */
public class PageEvenementNotFoundException extends RuntimeException {

    public PageEvenementNotFoundException(String slug) {
        super("Page d'événement introuvable : " + slug);
    }
}
