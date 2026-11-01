package net.franzka.sgilt.core.pay;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Client HTTP déclaratif vers sgilt-pay, seul service à parler à Stripe. Appelé sur le réseau
 * interne, sans authentification. Instancié comme bean Spring via {@link PayConfig}.
 */
@HttpExchange
public interface PayClient {

    /**
     * Crée le PaymentIntent d'une commande. Idempotent côté sgilt-pay : un second appel pour la même
     * commande renvoie le même PaymentIntent.
     *
     * @param request la commande et son montant
     * @return le secret client du PaymentIntent
     */
    @PostExchange("/internal/payment-intents")
    PaymentIntentResponse createPaymentIntent(@RequestBody PaymentIntentRequest request);
}
