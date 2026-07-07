package net.franzka.sgilt.core.admin.exception;

/**
 * Exception levée lorsque le slug demandé pour un prestataire est déjà utilisé en base.
 */
public class SlugAlreadyExistsException extends RuntimeException {

    /**
     * Construit l'exception pour le slug en conflit.
     *
     * @param slug le slug déjà existant
     */
    public SlugAlreadyExistsException(String slug) {
        super("Le slug '" + slug + "' est déjà utilisé.");
    }
}
