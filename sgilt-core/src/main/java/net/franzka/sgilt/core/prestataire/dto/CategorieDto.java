package net.franzka.sgilt.core.prestataire.dto;

import java.util.List;

/**
 * DTO d'une catégorie du référentiel prestataire, avec ses sous-catégories triées.
 */
public record CategorieDto(
        String key,
        String name,
        List<SousCategorieDto> subcategories
) {}
