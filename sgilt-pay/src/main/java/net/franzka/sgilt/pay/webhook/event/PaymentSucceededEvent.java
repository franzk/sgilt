package net.franzka.sgilt.pay.webhook.event;

import java.util.UUID;

/**
 * Paiement réussi d'une commande, publié vers sgilt-core. Même forme JSON que l'équivalent côté
 * sgilt-core.
 *
 * @param commandeId      l'identifiant de la commande payée
 * @param paymentIntentId l'identifiant du PaymentIntent Stripe qui l'a payée
 */
public record PaymentSucceededEvent(
        UUID commandeId,
        String paymentIntentId
) {}
