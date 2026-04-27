package net.franzka.sgilt.core.prestataire.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO de réponse pour la recherche de prestataires.
 * countsByCategory : compteurs par categoryKey (+ "all")
 * subcatCounts     : compteurs par UUID de sous-catégorie (uniquement pour la catégorie active)
 */
public record PrestataireSearchResponseDto(
        List<PrestataireCardDto> results,
        Map<String, Long> countsByCategory,
        Map<String, Long> subcatCounts
) {}
