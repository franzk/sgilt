package net.franzka.sgilt.core.evenement.billetterie.commande.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotPaidException;
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

    /**
     * Commande introuvable → 404 Not Found.
     *
     * @param ex l'exception levée
     * @return 404 Not Found
     */
    @ExceptionHandler(CommandeNotFoundException.class)
    public ResponseEntity<Void> handleCommandeNotFound(CommandeNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Commande pas payée, donc sans e-billet → 404 Not Found (pour l'acheteur, son e-billet
     * n'existe pas encore).
     *
     * @param ex l'exception levée
     * @return 404 Not Found
     */
    @ExceptionHandler(CommandeNotPaidException.class)
    public ResponseEntity<Void> handleCommandeNotPaid(CommandeNotPaidException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
