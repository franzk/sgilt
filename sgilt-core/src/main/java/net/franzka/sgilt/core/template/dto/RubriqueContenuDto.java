package net.franzka.sgilt.core.template.dto;

import java.util.List;

/**
 * Ce que regroupe une rubrique : de quoi limiter une recherche de prestataires à la rubrique.
 *
 * @param categories    les clés des catégories entières
 * @param subcategories les clés des sous-catégories isolées
 */
public record RubriqueContenuDto(List<String> categories, List<String> subcategories) {}
