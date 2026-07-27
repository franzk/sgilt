package net.franzka.sgilt.core.prestataire.dto;

/**
 * Compteurs de réservations par statut pour un prestataire, pour la liste admin.
 */
public record PrestataireReservationCountsDto(
        int confirmedCount,
        int inDiscussionCount,
        int nouvelleCount,
        int refuseeCount,
        int annuleeCount,
        int realiseeCount
) {}
