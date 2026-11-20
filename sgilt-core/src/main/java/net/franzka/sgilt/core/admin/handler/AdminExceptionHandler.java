package net.franzka.sgilt.core.admin.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.exception.ClientInvitationNotSentException;
import net.franzka.sgilt.core.admin.exception.EmailAlreadyUsedException;
import net.franzka.sgilt.core.admin.exception.SlugAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mapping HTTP des exceptions du domaine admin.
 */
@RestControllerAdvice
@Slf4j
public class AdminExceptionHandler {

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<Void> handleSlugAlreadyExists(SlugAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Void> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(ClientInvitationNotSentException.class)
    public ResponseEntity<Void> handleClientInvitationNotSent(ClientInvitationNotSentException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}
