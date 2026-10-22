package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Objects;

/**
 * Rubrique d'un événement pas encore créé, avec les demandes initiées qui y sont rangées.
 *
 * @param key          la clé de la rubrique ('lieu', 'autre'…)
 * @param reservations les demandes initiées de la rubrique
 */
public record RubriqueDto(
        @NotBlank String key,
        @Valid List<DemandeInitieeDto> reservations
) {

    /**
     * Toutes les demandes initiées d'un ensemble de rubriques, rubrique par rubrique.
     *
     * @param rubriques les rubriques, éventuellement {@code null}
     * @return les demandes initiées, vide s'il n'y en a pas
     */
    public static List<DemandeInitieeDto> demandesInitiees(List<RubriqueDto> rubriques) {
        if (rubriques == null) return List.of();
        return rubriques.stream()
                .map(RubriqueDto::reservations)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }
}
