package net.franzka.sgilt.core.reservation.domain;

/**
 * Type de rappel automatique envoyé pour une réservation — un rappel d'un type donné ne part
 * qu'une fois par réservation (voir {@link ReservationReminder}).
 */
public enum ReservationReminderType {

    /** Rappel au client d'une réservation restée NEW (en attente de contact) depuis 7 jours. */
    CONTACT_PENDING
}
