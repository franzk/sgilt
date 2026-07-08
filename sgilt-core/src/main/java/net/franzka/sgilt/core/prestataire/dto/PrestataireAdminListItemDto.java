package net.franzka.sgilt.core.prestataire.dto;

import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;

import java.util.UUID;

/**
 * DTO allégé pour l'affichage en liste dans le back-office admin.
 */
public record PrestataireAdminListItemDto(
        UUID id,
        String name,
        String slug,
        PrestataireStatus status
) {}
