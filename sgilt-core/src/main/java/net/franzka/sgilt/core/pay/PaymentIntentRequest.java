package net.franzka.sgilt.core.pay;

import java.util.UUID;

/**
 * Requête de création d'un PaymentIntent, envoyée à sgilt-pay.
 *
 * @param commandeId  l'identifiant de la commande à payer
 * @param amountCents le montant à payer, en centimes d'euro
 */
public record PaymentIntentRequest(
        UUID commandeId,
        int amountCents
) {}
