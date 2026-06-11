package net.franzka.sgilt.core.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddNoteRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull Boolean isPersonal
) {}
