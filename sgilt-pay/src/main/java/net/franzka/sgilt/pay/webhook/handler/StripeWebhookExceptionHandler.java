package net.franzka.sgilt.pay.webhook.handler;

import com.stripe.exception.SignatureVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class StripeWebhookExceptionHandler {

    /**
     * Signature Stripe invalide (appel qui ne vient pas de Stripe, ou mauvais secret) → 400 Bad Request.
     *
     * @param ex l'exception levée
     * @return 400 Bad Request
     */
    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<Void> handleSignature(SignatureVerificationException ex) {
        log.warn("Webhook Stripe rejeté, signature invalide : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
