package net.franzka.sgilt.core.reservation.dto;

import java.util.UUID;

/**
 * En-tête d'une réservation pour le client : le prestataire (dont son slug, pour charger sa fiche
 * publique dans l'onglet Prestataire), la catégorie et le statut.
 */
public record ReservationMetaDto(
        UUID id,
        UUID prestataireId,
        String prestataireName,
        String prestataireSlug,
        String prestatairePhoto,
        String category,
        String status,
        int unreadNotesCount
) {}
