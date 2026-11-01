package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;

import java.util.UUID;

/**
 * Commande de billets telle que relue par l'écran de confirmation. Sans donnée de l'acheteur :
 * l'identifiant de la commande suffit à la lire.
 *
 * @param id             l'identifiant de la commande
 * @param status         son statut (PAID une fois le paiement confirmé par Stripe)
 * @param quantity       le nombre de billets
 * @param unitPriceCents le prix d'un billet à la commande, en centimes d'euro
 * @param totalCents     le montant total, en centimes d'euro
 */
public record CommandeSummaryDto(
        UUID id,
        CommandeStatus status,
        int quantity,
        int unitPriceCents,
        int totalCents
) {}
