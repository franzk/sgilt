package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddReservationRequest(
        @NotNull UUID prestataireId,
        String message
) {}
