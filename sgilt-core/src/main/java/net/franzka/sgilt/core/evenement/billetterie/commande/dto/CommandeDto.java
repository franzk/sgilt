package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Commande de places telle que renvoyée à l'acheteur.
 *
 * @param id           l'identifiant de la commande
 * @param quantity     le nombre de places
 * @param totalCents   le montant total, en centimes d'euro
 * @param expiresAt    la fin du blocage de ses places, faute de paiement
 * @param clientSecret le secret client du PaymentIntent Stripe, que le front passe à Stripe.js
 */
public record CommandeDto(
        UUID id,
        int quantity,
        int totalCents,
        LocalDateTime expiresAt,
        String clientSecret
) {}
