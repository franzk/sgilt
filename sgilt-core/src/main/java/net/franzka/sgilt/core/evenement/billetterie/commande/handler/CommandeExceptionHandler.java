package net.franzka.sgilt.core.evenement.billetterie.commande.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CommandeExceptionHandler {

    /**
     * Plus assez de places pour la commande → 409 Conflict.
     *
     * @param ex l'exception levée
     * @return 409 Conflict
     */
    @ExceptionHandler(NotEnoughTicketsException.class)
    public ResponseEntity<Void> handleNotEnoughTickets(NotEnoughTicketsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
