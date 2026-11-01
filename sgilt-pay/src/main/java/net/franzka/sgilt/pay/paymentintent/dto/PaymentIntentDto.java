package net.franzka.sgilt.pay.paymentintent.dto;

/**
 * PaymentIntent créé, tel que renvoyé à sgilt-core.
 *
 * @param clientSecret le secret client, que le front passe à Stripe.js pour confirmer le paiement
 */
public record PaymentIntentDto(
        String clientSecret
) {}
