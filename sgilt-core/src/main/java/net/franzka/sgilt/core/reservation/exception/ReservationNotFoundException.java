package net.franzka.sgilt.core.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException() {
        super("Réservation introuvable");
    }
}
