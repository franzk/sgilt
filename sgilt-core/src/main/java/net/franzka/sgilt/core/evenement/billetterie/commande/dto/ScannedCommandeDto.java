package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import java.time.LocalDateTime;

/**
 * Commande dont l'e-billet vient d'être scanné, telle que la voit l'organisateur à l'entrée.
 *
 * @param buyerFirstName le prénom de l'acheteur
 * @param buyerLastName  le nom de l'acheteur
 * @param quantity       le nombre de places
 * @param scannedAt      l'heure du premier scan (celle de ce scan s'il vient de la faire entrer),
 *                       null si jamais scannée
 */
public record ScannedCommandeDto(
        String buyerFirstName,
        String buyerLastName,
        int quantity,
        LocalDateTime scannedAt
) {}
