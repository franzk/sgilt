package net.franzka.sgilt.core.pay;

import java.util.UUID;

/**
 * Paiement réussi d'une commande, publié par sgilt-pay à la réception du webhook Stripe sur la queue
 * {@value PaymentEventsConfig#PAYMENT_SUCCEEDED_QUEUE}. Même forme JSON que l'équivalent côté sgilt-pay.
 *
 * @param commandeId      l'identifiant de la commande payée
 * @param paymentIntentId l'identifiant du PaymentIntent Stripe qui l'a payée
 */
public record PaymentSucceededEvent(
        UUID commandeId,
        String paymentIntentId
) {}
