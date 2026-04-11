package net.franzka.sgilt.core.reservation.exception;

/**
 * Exception levée lorsqu'une entité n'est pas dans l'état requis pour effectuer l'opération demandée.
 */
public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }
}
