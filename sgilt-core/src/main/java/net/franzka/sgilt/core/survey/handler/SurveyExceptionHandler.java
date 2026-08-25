package net.franzka.sgilt.core.survey.handler;

import net.franzka.sgilt.core.survey.exception.SurveyNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SurveyExceptionHandler {

    /**
     * Sondage introuvable (slug inconnu) → 404.
     */
    @ExceptionHandler(SurveyNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(SurveyNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
