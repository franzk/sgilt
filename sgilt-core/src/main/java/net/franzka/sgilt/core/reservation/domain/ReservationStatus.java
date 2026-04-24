package net.franzka.sgilt.core.reservation.domain;

public enum ReservationStatus {
    NEW,
    IN_DISCUSSION,
    CONFIRMED,
    DONE,
    REFUSED_PRE_CONTACT,
    REFUSED_POST_CONTACT,
    CANCELED_BY_CLIENT_PRE_CONTACT,
    CANCELED_BY_CLIENT_POST_CONTACT,
    CANCELED_POST_CONFIRMATION
}
