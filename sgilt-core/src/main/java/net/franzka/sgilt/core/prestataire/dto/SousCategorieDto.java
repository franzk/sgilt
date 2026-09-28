package net.franzka.sgilt.core.prestataire.dto;

/**
 * DTO d'une sous-catégorie du référentiel prestataire.
 */
public record SousCategorieDto(
        String key,
        String name,
        String categoryKey
) {}
