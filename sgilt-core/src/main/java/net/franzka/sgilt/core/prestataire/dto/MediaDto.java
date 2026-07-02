package net.franzka.sgilt.core.prestataire.dto;

import net.franzka.sgilt.core.prestataire.domain.MediaType;

/**
 * DTO représentant un média ordonné d'un prestataire.
 * position 0 = hero (toujours IMAGE).
 */
public record MediaDto(MediaType type, String ref, int position) {}
