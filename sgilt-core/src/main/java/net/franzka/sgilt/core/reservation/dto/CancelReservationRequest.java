package net.franzka.sgilt.core.reservation.dto;

public record CancelReservationRequest(
        String reason,
        boolean isPersonal
) {}
