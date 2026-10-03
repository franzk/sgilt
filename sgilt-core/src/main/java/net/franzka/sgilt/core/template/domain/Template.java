package net.franzka.sgilt.core.template.domain;

import java.util.List;

/**
 * Template d'un type d'événement : ses rubriques, dans l'ordre d'affichage.
 *
 * @param type      le type d'événement ('mariage'…)
 * @param rubriques les rubriques du template
 */
public record Template(String type, List<TemplateRubrique> rubriques) {}
