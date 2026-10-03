package net.franzka.sgilt.core.evenement.domain;

/**
 * Rubrique propre à un événement, copiée de son template à la création. Son état (retirée,
 * « plus tard »…) viendra ici.
 *
 * @param key la clé de la rubrique ('lieu'…)
 */
public record EvenementRubrique(String key) {}
