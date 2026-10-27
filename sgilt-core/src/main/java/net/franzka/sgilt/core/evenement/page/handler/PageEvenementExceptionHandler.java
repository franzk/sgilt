package net.franzka.sgilt.core.evenement.page.handler;

import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PageEvenementExceptionHandler {

    /**
     * Page d'événement introuvable → 404.
     *
     * @param ex l'exception levée
     * @return 404 No Content
     */
    @ExceptionHandler(PageEvenementNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(PageEvenementNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
