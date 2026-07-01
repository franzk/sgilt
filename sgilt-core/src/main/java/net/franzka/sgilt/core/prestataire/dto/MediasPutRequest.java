package net.franzka.sgilt.core.prestataire.dto;

import java.util.List;

/**
 * Corps de la requête PUT /prestataires/ma-fiche/medias.
 * Représente la collection complète de médias à persister (remplacement total).
 *
 * @param medias liste ordonnée des médias — position 0 doit être de type IMAGE
 */
public record MediasPutRequest(List<MediaDto> medias) {}
