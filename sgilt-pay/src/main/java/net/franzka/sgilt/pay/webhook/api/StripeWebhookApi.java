package net.franzka.sgilt.pay.webhook.api;

import com.stripe.exception.SignatureVerificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

// Seul endpoint public de sgilt-pay : appelé par Stripe via nginx et la gateway. Pas
// d'authentification, la signature Stripe en tient lieu.
@RequestMapping("webhooks/stripe")
public interface StripeWebhookApi {

    // Corps brut : la signature porte sur les octets exacts reçus, il ne doit pas être désérialisé avant.
    @PostMapping
    ResponseEntity<Void> handleEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature)
            throws SignatureVerificationException;
}
