package net.franzka.sgilt.core.prestataire.dto;

/**
 * DTO d'une sous-catégorie pour l'administration, avec le nombre de prestataires qui l'utilisent.
 */
public record SousCategorieAdminDto(
        String key,
        String name,
        String categoryKey,
        long prestataireCount
) {}
