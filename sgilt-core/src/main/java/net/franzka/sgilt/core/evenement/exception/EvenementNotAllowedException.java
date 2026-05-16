package net.franzka.sgilt.core.evenement.exception;

public class EvenementNotAllowedException extends RuntimeException {

    public EvenementNotAllowedException() {
        super("Accès à cet événement non autorisé");
    }
}
