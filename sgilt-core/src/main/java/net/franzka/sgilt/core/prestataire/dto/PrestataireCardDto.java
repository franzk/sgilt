package net.franzka.sgilt.core.prestataire.dto;

/**
 * DTO allégé pour l'affichage en liste (résultats de recherche).
 */
public record PrestataireCardDto(
        String id,
        String name,
        String shortDescription,
        String heroImage,
        String slug,
        String categoryKey
) {}
