package net.franzka.sgilt.core.prestataire.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mapping HTTP des exceptions du domaine prestataire.
 */
@RestControllerAdvice
public class PrestataireExceptionHandler {

    @ExceptionHandler(PrestataireNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(PrestataireNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
