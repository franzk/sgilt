package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.OrganizerCommandeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Commande d'une billetterie, dans la liste de l'organisateur de l'événement.
 *
 * @param id             l'identifiant de la commande
 * @param status         son statut, EXPIRED pour une commande en attente dont le blocage est passé
 * @param buyerFirstName le prénom de l'acheteur
 * @param buyerLastName  le nom de l'acheteur
 * @param buyerEmail     l'email de l'acheteur
 * @param quantity       le nombre de places
 * @param totalCents     le montant total, en centimes d'euro
 * @param createdAt      la date de la commande
 */
public record OrganizerCommandeDto(
        UUID id,
        OrganizerCommandeStatus status,
        String buyerFirstName,
        String buyerLastName,
        String buyerEmail,
        int quantity,
        int totalCents,
        LocalDateTime createdAt
) {}
