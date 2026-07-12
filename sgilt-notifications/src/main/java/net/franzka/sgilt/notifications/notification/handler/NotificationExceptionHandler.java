package net.franzka.sgilt.notifications.notification.handler;

import net.franzka.sgilt.notifications.notification.exception.NotificationAccessDeniedException;
import net.franzka.sgilt.notifications.notification.exception.NotificationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationExceptionHandler {

    /**
     * Notification introuvable → 404.
     */
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(NotificationNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    /**
     * Notification n'appartenant pas à l'appelant connecté → 403.
     */
    @ExceptionHandler(NotificationAccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(NotificationAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
