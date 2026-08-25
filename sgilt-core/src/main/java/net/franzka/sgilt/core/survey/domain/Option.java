package net.franzka.sgilt.core.survey.domain;

/**
 * Option proposée pour une question {@link QuestionType#SINGLE_CHOICE} ou
 * {@link QuestionType#MULTI_CHOICE}.
 *
 * @param value         valeur stockée dans {@code answers} si l'option est sélectionnée
 * @param label         texte affiché à l'utilisateur
 * @param allowFreeText si {@code true}, affiche un champ texte libre conditionnel quand l'option
 *                      est sélectionnée (cas "Autre")
 */
public record Option(
        String value,
        String label,
        boolean allowFreeText
) {}
