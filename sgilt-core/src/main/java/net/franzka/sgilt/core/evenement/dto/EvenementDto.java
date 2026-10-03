package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Événement complet pas encore créé : ses données et sa structure (rubriques et demandes
 * initiées). C'est ce qui est matérialisé à l'activation d'un onboarding.
 */
public record EvenementDto(
        @Size(max = 100) String eventType,
        @Size(max = 100) String ambiance,
        @Size(max = 100) String momentCle,
        @Size(max = 2000) String description,
        LocalDate date,
        @Size(max = 100) String ville,
        @Size(max = 20) String nbInvites,
        @Size(max = 200) String lieu,
        @Valid List<RubriqueDto> rubriques
) {}
