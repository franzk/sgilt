package net.franzka.sgilt.core.prestataire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Requête de modification d'une sous-catégorie : libellé et catégorie. Changer de catégorie
 * déplace ses prestataires avec elle.
 */
public record SousCategorieUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String categoryKey
) {}
