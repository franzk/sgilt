package net.franzka.sgilt.core.reservation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProReservationSummaryDto(
        UUID id,
        String evenementTitre,
        String image,
        LocalDate datePrestation,
        String statut,
        int unreadNotesCount
) {}
