package net.franzka.sgilt.core.reservation.event;

/**
 * Type d'élément ajouté au feed d'une réservation — porté explicitement dans le payload de
 * {@code ReservationFeedItemAddedEvent} pour dériver le {@code NotificationType} et la clé i18n
 * côté sgilt-notifications.
 */
public enum FeedItemType {
    NOTE,
    DOCUMENT
}
