package net.franzka.sgilt.core.ficheia.dto;

import net.franzka.sgilt.core.ficheia.domain.FicheIaAction;
import net.franzka.sgilt.core.ficheia.domain.FicheIaSection;

/**
 * Corps de requête de l'endpoint d'application du résultat de génération IA à la fiche.
 * Ne porte qu'une instruction (section + action) — les valeurs appliquées sont lues côté serveur,
 * jamais transmises par le client. {@code section} est {@code null} uniquement pour ECRASER_TOUT.
 */
public record FicheIaApplyRequest(FicheIaSection section, FicheIaAction action) {}
