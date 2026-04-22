package net.franzka.sgilt.core.onboarding.handler;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.reservation.exception.InvalidStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OnboardingExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Void> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).build();
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<Void> handleTokenAlreadyUsed(TokenAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Void> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<Void> handleInvalidState(InvalidStateException ex) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Void> handleJwtException(JwtException ex) {
        return ResponseEntity.badRequest().build();
    }
}
