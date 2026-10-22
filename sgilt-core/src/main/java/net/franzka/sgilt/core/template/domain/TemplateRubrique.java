package net.franzka.sgilt.core.template.domain;

import java.util.List;

/**
 * Rubrique d'un template : sa clé et ce qu'elle regroupe (catégories entières et sous-catégories
 * isolées). Le libellé est porté par l'i18n du front, d'après la clé.
 *
 * @param key           la clé de la rubrique ('musique-animation'…)
 * @param categories    les clés des catégories entières
 * @param subcategories les clés des sous-catégories isolées
 */
public record TemplateRubrique(String key, List<String> categories, List<String> subcategories) {}
