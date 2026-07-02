package net.franzka.sgilt.core.prestataire.exception;

/**
 * Exception levée lorsque la collection de médias soumise enfreint les règles métier.
 * Ex : absence d'une image principale en position 0.
 */
public class MediasInvalidException extends RuntimeException {

    public MediasInvalidException(String message) {
        super(message);
    }
}
