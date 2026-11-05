package net.franzka.sgilt.core.evenement.billetterie.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieOpenWithoutPublishedPageException;
import net.franzka.sgilt.core.evenement.billetterie.exception.CapacityBelowSoldTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieWithSalesException;
import org.springframework.http.HttpStatus;
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

    /**
     * Désactivation d'une billetterie qui a des ventes → 409.
     *
     * @param ex l'exception levée
     * @return 409 Conflict
     */
    @ExceptionHandler(BilletterieWithSalesException.class)
    public ResponseEntity<Void> handleWithSales(BilletterieWithSalesException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Jauge sous les places vendues → 409.
     *
     * @param ex l'exception levée
     * @return 409 Conflict
     */
    @ExceptionHandler(CapacityBelowSoldTicketsException.class)
    public ResponseEntity<Void> handleCapacityBelowSold(CapacityBelowSoldTicketsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Billetterie ouverte sans page publiée → 400.
     *
     * @param ex l'exception levée
     * @return 400 Bad Request
     */
    @ExceptionHandler(BilletterieOpenWithoutPublishedPageException.class)
    public ResponseEntity<Void> handleOpenWithoutPublishedPage(BilletterieOpenWithoutPublishedPageException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
