package net.franzka.sgilt.core.prestataire.dto;

import java.util.List;

/**
 * DTO d'une catégorie pour l'administration, avec ses sous-catégories triées.
 */
public record CategorieAdminDto(
        String key,
        String name,
        List<SousCategorieAdminDto> subcategories
) {}
