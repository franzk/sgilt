package net.franzka.sgilt.core.evenement.handler;

import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EvenementExceptionHandler {

    /**
     * Événement introuvable → 404.
     *
     * @param ex l'exception levée
     * @return 404 No Content
     */
    @ExceptionHandler(EvenementNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(EvenementNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    /**
     * Événement trouvé mais n'appartenant pas à l'utilisateur connecté → 403.
     *
     * @param ex l'exception levée
     * @return 403 Forbidden
     */
    @ExceptionHandler(EvenementNotAllowedException.class)
    public ResponseEntity<Void> handleNotAllowed(EvenementNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
