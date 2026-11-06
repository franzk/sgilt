package net.franzka.sgilt.pay.paymentintent.handler;

import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class PaymentIntentExceptionHandler {

    /**
     * Échec de l'appel à Stripe → 502 Bad Gateway.
     *
     * @param ex l'exception levée
     * @return 502 Bad Gateway
     */
    @ExceptionHandler(StripeException.class)
    public ResponseEntity<Void> handleStripe(StripeException ex) {
        log.error("Échec de l'appel à Stripe — code={} requestId={} : {}",
                ex.getCode(), ex.getRequestId(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}
