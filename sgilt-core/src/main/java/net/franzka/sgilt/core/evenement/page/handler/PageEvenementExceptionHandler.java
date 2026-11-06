package net.franzka.sgilt.core.evenement.page.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.page.exception.BilletterieWithoutPageException;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.exception.PageNotCreatedException;
import net.franzka.sgilt.core.evenement.page.exception.PagePubliqueDisabledException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugAlreadyUsedException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugLockedException;
import org.springframework.http.HttpStatus;
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

    /**
     * Page publique pas activée par le back-office → 404.
     *
     * @param ex l'exception levée
     * @return 404 Not Found
     */
    @ExceptionHandler(PagePubliqueDisabledException.class)
    public ResponseEntity<Void> handlePagePubliqueDisabled(PagePubliqueDisabledException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    /**
     * Page pas encore créée → 404.
     *
     * @param ex l'exception levée
     * @return 404 Not Found
     */
    @ExceptionHandler(PageNotCreatedException.class)
    public ResponseEntity<Void> handlePageNotCreated(PageNotCreatedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    /**
     * Changement du slug d'une page déjà publiée → 409.
     *
     * @param ex l'exception levée
     * @return 409 Conflict
     */
    @ExceptionHandler(PageSlugLockedException.class)
    public ResponseEntity<Void> handleSlugLocked(PageSlugLockedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
