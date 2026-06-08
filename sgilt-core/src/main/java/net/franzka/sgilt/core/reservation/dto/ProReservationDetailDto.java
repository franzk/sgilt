package net.franzka.sgilt.core.reservation.dto;

import java.time.LocalDate;
import java.util.UUID;

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
        String clientFirstName,
        String clientLastName,
        String clientPhone,
        String clientEmail
) {}
