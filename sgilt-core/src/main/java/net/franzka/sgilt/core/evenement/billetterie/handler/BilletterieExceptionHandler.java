package net.franzka.sgilt.core.evenement.billetterie.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BilletterieExceptionHandler {

    /**
     * Événement sans billetterie → 404.
     *
     * @param ex l'exception levée
     * @return 404 Not Found
     */
    @ExceptionHandler(BilletterieNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(BilletterieNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    /**
     * Billetterie pas ouverte → 400.
     *
     * @param ex l'exception levée
     * @return 400 Bad Request
     */
    @ExceptionHandler(BilletterieNotOpenException.class)
    public ResponseEntity<Void> handleNotOpen(BilletterieNotOpenException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
