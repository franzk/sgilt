package net.franzka.sgilt.core.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Détail d'une réservation pour le prestataire : la réservation, l'événement complet (les mêmes
 * informations que le client voit dans ses paramètres) et les coordonnées du client.
 */
public record ProReservationDetailDto(
        UUID id,
        String statut,
        String category,
        String prestataireName,
        String prestatairePhoto,
        String evenementTitre,
        String evenementType,
        String evenementImagePath,
        LocalDate evenementDate,
        String evenementVille,
        UUID evenementId,
        String evenementLieu,
        String evenementNbInvites,
        String evenementAmbiance,
        String evenementMomentCle,
        String evenementDescription,
        String evenementNotePartagee,
        LocalDateTime evenementLastUpdateDate,
        String clientFirstName,
        String clientLastName,
        String clientPhone,
        String clientEmail
) {}
