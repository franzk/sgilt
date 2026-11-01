package net.franzka.sgilt.pay.webhook.controller;

import com.stripe.exception.SignatureVerificationException;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.pay.webhook.api.StripeWebhookApi;
import net.franzka.sgilt.pay.webhook.service.StripeWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP des webhooks Stripe.
 */
@RestController
@RequiredArgsConstructor
public class StripeWebhookController implements StripeWebhookApi {

    private final StripeWebhookService stripeWebhookService;

    /**
     * Reçoit un événement Stripe.
     *
     * @param payload   le corps brut de la requête
     * @param signature l'en-tête Stripe-Signature
     * @return 200 OK une fois l'événement traité, ou ignoré s'il ne nous concerne pas
     * @throws SignatureVerificationException si la signature est invalide
     */
    @Override
    public ResponseEntity<Void> handleEvent(String payload, String signature) throws SignatureVerificationException {
        stripeWebhookService.handleEvent(payload, signature);
        return ResponseEntity.ok().build();
    }
}
