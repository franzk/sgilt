package net.franzka.sgilt.core.prestataire.dto;

import net.franzka.sgilt.core.prestataire.domain.DetailCategory;

/**
 * DTO représentant une information libre (détail) sur un prestataire.
 */
public record DetailDto(String content, DetailCategory category) {}
