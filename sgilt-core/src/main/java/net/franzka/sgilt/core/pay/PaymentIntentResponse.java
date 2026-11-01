package net.franzka.sgilt.core.pay;

/**
 * PaymentIntent créé par sgilt-pay.
 *
 * @param clientSecret le secret client, que le front passe à Stripe.js pour confirmer le paiement
 */
public record PaymentIntentResponse(
        String clientSecret
) {}
