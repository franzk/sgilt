package net.franzka.sgilt.core.reservation.dto;

import java.util.List;

public record ActiveReservationsDto(
        List<ActiveReservationItemDto> items,
        boolean hasConfirmed
) {}