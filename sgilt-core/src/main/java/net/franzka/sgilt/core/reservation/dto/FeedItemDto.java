package net.franzka.sgilt.core.reservation.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Représente un élément du feed d'une réservation (note ou envoi de documents).
 * Le champ {@code type} détermine le type de l'item ("note" | "document").
 * Les champs non applicables au type sont null.
 * <p>
 * Un élément {@code document} est un envoi : les documents déposés ensemble ({@code upload_id}
 * commun), dans {@code files}. Son {@code id} est l'{@code uploadId}, ou celui du document pour un
 * document antérieur à l'envoi multiple.
 */
@Builder
public record FeedItemDto(

        // ── Commun ──────────────────────────────────────────────────────────────
        String type,
        UUID id,
        String authorId,
        String authorName,
        String authorPhoto,
        String authorRole,
        LocalDateTime createdAt,
        String title,

        // ── Note ────────────────────────────────────────────────────────────────
        String content,
        Boolean isPersonal,
        Boolean isMessageInitial,

        // ── Document : les fichiers de l'envoi ──────────────────────────────────
        List<FeedFileDto> files,

        // ── Note système ──────────────────────────────────────────────────────────
        String generatedKey
) {}
