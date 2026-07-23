package net.franzka.sgilt.core.ficheia.dto;

import java.time.LocalDateTime;

/**
 * État de la génération IA de fiche exposé au frontend : le résultat généré (jamais nul pour
 * l'endpoint de génération, nul si aucune génération n'existe pour l'endpoint de lecture d'état),
 * le quota restant (toujours renseigné, valeur par défaut si aucune ligne n'existe encore en base)
 * et l'horodatage de la dernière génération réussie (nul si aucune génération n'existe).
 */
public record FicheIaGenerationResultDto(
        FicheIaGenerationDto result,
        int triesLeft,
        LocalDateTime lastGenerationDateTime) {}
