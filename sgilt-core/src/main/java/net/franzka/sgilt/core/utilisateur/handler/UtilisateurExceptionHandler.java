package net.franzka.sgilt.core.utilisateur.handler;

import net.franzka.sgilt.core.utilisateur.exception.UtilisateurNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UtilisateurExceptionHandler {

    /**
     * Utilisateur introuvable en base → 404.
     *
     * @param ex l'exception levée
     * @return 404 No Content
     */
    @ExceptionHandler(UtilisateurNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(UtilisateurNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
