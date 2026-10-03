package net.franzka.sgilt.core.prestataire.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mapping HTTP des exceptions du domaine prestataire.
 */
@RestControllerAdvice
@Slf4j
public class PrestataireExceptionHandler {

    @ExceptionHandler(PrestataireNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(PrestataireNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(PrestataireForbiddenException.class)
    public ResponseEntity<Void> handleForbidden(PrestataireForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(MediasInvalidException.class)
    public ResponseEntity<Void> handleMediasInvalid(MediasInvalidException ex) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SousCategorieNotFoundException.class)
    public ResponseEntity<Void> handleSousCategorieNotFound(SousCategorieNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(SousCategorieInvalidException.class)
    public ResponseEntity<Void> handleSousCategorieInvalid(SousCategorieInvalidException ex) {
        log.warn("{} — {}", ex.getReason(), ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(PrestataireInvalidStateException.class)
    public ResponseEntity<Void> handleInvalidState(PrestataireInvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
