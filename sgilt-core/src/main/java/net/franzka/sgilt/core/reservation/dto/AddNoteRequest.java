package net.franzka.sgilt.core.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record AddNoteRequest(
        @NotBlank String title,
        @NotBlank String content,
        Boolean isPersonal
) {}
