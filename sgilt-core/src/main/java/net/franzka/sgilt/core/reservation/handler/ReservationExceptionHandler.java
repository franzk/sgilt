package net.franzka.sgilt.core.reservation.handler;

import net.franzka.sgilt.core.reservation.exception.InvalidStateException;
import net.franzka.sgilt.core.reservation.exception.ReservationFeedItemNotFoundException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotAllowedException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotFoundException;
import net.franzka.sgilt.core.storage.FileTooLargeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ReservationExceptionHandler {

    /**
     * Réservation introuvable → 404.
     */
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ReservationNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    /**
     * Élément du feed introuvable → 404.
     */
    @ExceptionHandler(ReservationFeedItemNotFoundException.class)
    public ResponseEntity<Void> handleFeedItemNotFound(ReservationFeedItemNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    /**
     * Réservation n'appartenant pas à l'utilisateur connecté → 403.
     */
    @ExceptionHandler(ReservationNotAllowedException.class)
    public ResponseEntity<Void> handleNotAllowed(ReservationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Transition de statut invalide → 409 Conflict.
     */
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<Void> handleInvalidState(InvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Fichier trop volumineux → 413 Payload Too Large.
     */
    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<Void> handleFileTooLarge(FileTooLargeException ex) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).build();
    }

    /**
     *   Fichier dépassant la limite de taille configurée → 413 Payload Too Large.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Void> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).build();
    }
}
