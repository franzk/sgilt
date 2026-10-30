package net.franzka.sgilt.core.evenement.page.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.page.exception.BilletterieWithoutPageException;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugAlreadyUsedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
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

    /**
     * Slug de page déjà pris par un autre événement → 400.
     *
     * @param ex l'exception levée
     * @return 400 Bad Request
     */
    @ExceptionHandler(PageSlugAlreadyUsedException.class)
    public ResponseEntity<Void> handleSlugAlreadyUsed(PageSlugAlreadyUsedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    /**
     * Billetterie saisie sans page → 400.
     *
     * @param ex l'exception levée
     * @return 400 Bad Request
     */
    @ExceptionHandler(BilletterieWithoutPageException.class)
    public ResponseEntity<Void> handleBilletterieWithoutPage(BilletterieWithoutPageException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
