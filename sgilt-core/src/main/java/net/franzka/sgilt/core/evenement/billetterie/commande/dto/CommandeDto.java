package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Commande de billets telle que renvoyée à l'acheteur.
 *
 * @param id         l'identifiant de la commande
 * @param quantity   le nombre de billets
 * @param totalCents le montant total, en centimes d'euro
 * @param expiresAt  la fin du blocage de ses places, faute de paiement
 */
public record CommandeDto(
        UUID id,
        int quantity,
        int totalCents,
        LocalDateTime expiresAt
) {}
