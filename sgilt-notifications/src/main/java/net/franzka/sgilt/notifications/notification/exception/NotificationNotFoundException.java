package net.franzka.sgilt.notifications.notification.exception;

import java.util.UUID;

/**
 * Levée quand aucune notification ne correspond à l'identifiant demandé.
 */
public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(UUID notificationId) {
        super("Notification introuvable : " + notificationId);
    }
}
