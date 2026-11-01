package net.franzka.sgilt.pay.paymentintent.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

/**
 * Requête de création d'un PaymentIntent, envoyée par sgilt-core au clic sur « Payer ».
 *
 * @param commandeId  l'identifiant de la commande à payer
 * @param amountCents le montant à payer, en centimes d'euro
 */
public record CreatePaymentIntentRequest(
        @NotNull UUID commandeId,
        @Positive int amountCents
) {}
