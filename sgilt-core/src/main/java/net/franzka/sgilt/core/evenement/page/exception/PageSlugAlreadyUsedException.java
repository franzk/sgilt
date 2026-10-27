package net.franzka.sgilt.core.evenement.page.exception;

/**
 * Exception levée lorsque le slug demandé pour la page d'un événement est déjà celui de la page
 * d'un autre événement.
 */
public class PageSlugAlreadyUsedException extends RuntimeException {

    /**
     * Construit l'exception pour le slug en conflit.
     *
     * @param slug le slug déjà utilisé
     */
    public PageSlugAlreadyUsedException(String slug) {
        super("Le slug de page '" + slug + "' est déjà utilisé par un autre événement.");
    }
}
