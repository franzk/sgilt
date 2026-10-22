package net.franzka.sgilt.core.prestataire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Requête de création d'une sous-catégorie. La clé, en kebab-case, n'est plus modifiable ensuite :
 * elle est référencée par les prestataires et les URLs de recherche.
 */
public record SousCategorieCreateRequest(
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$") String key,
        @NotBlank @Size(max = 100) String name,
        @NotBlank String categoryKey
) {}
