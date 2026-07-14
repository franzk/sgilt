package net.franzka.sgilt.notifications.notification.domain;

/**
 * Type de notification, miroir du type {@code NotificationType} côté front
 * (`sgilt-front/app/types/notification.ts`).
 */
public enum NotificationType {
    STATE_CHANGE,
    NEW_NOTE,
    NEW_DOCUMENT,
    NEW_REQUEST
}
