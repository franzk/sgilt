package net.franzka.sgilt.core.admin.handler;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.exception.SlugAlreadyExistsException;
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
}
