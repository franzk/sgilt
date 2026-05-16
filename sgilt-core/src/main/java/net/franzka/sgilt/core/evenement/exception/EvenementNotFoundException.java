package net.franzka.sgilt.core.evenement.exception;

public class EvenementNotFoundException extends RuntimeException {

    public EvenementNotFoundException() {
        super("Événement introuvable");
    }
}
