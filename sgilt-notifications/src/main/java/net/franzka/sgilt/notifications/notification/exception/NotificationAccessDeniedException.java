package net.franzka.sgilt.notifications.notification.exception;

import java.util.UUID;

/**
 * Levée quand l'appelant authentifié n'est pas le destinataire de la notification demandée.
 */
public class NotificationAccessDeniedException extends RuntimeException {

    public NotificationAccessDeniedException(UUID notificationId) {
        super("Accès refusé à la notification : " + notificationId);
    }
}
