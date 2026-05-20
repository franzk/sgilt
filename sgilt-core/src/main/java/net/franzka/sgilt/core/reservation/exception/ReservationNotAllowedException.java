package net.franzka.sgilt.core.reservation.exception;

public class ReservationNotAllowedException extends RuntimeException {
    public ReservationNotAllowedException() {
        super("Accès à la réservation non autorisé");
    }
}
