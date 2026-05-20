package net.franzka.sgilt.core.reservation.exception;

public class ReservationFeedItemNotFoundException extends RuntimeException {
    public ReservationFeedItemNotFoundException() {
        super("Élément du feed introuvable");
    }
}
