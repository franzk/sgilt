package net.franzka.sgilt.core.ficheia.dto;

/**
 * Réponse de l'endpoint de génération IA de fiche : le résultat généré et le quota restant après
 * décrément, pour permettre au frontend de mettre à jour son affichage sans appel supplémentaire.
 */
public record FicheIaGenerationResultDto(FicheIaGenerationDto result, int triesLeft) {}
