package net.franzka.sgilt.core.prestataire.exception;

/**
 * Levée lorsqu'une fiche prestataire n'est pas dans le statut requis pour effectuer la transition demandée.
 */
public class PrestataireInvalidStateException extends RuntimeException {

    public PrestataireInvalidStateException(String message) {
        super(message);
    }
}
