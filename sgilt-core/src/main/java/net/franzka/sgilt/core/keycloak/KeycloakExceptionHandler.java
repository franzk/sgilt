package net.franzka.sgilt.core.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mapping HTTP des exceptions du domaine Keycloak.
 */
@RestControllerAdvice
@Slf4j
public class KeycloakExceptionHandler {

    @ExceptionHandler(KeycloakUserAlreadyExistsException.class)
    public ResponseEntity<Void> handleUserAlreadyExists(KeycloakUserAlreadyExistsException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
