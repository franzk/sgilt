package net.franzka.sgilt.core.prestataire.exception;

/**
 * Levée quand aucun prestataire ne correspond au slug demandé.
 */
public class PrestataireNotFoundException extends RuntimeException {

    public PrestataireNotFoundException(String slug) {
        super("Prestataire introuvable : " + slug);
    }
}
