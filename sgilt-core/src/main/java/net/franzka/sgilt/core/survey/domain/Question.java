package net.franzka.sgilt.core.survey.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Question d'un sondage.
 *
 * @param id          identifiant unique de la question au sein du sondage — clé utilisée dans
 *                    {@code answers} à la soumission
 * @param type        type de la question
 * @param label       intitulé affiché
 * @param required    si {@code true}, une réponse non vide est exigée à la soumission
 * @param options     options proposées — uniquement pour {@code single_choice}/{@code multi_choice},
 *                    {@code null} sinon
 * @param placeholder texte d'aide affiché dans le champ vide — uniquement pour {@code open_text},
 *                    {@code null} sinon
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Question(
        String id,
        QuestionType type,
        String label,
        boolean required,
        List<Option> options,
        String placeholder
) {}
