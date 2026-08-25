package net.franzka.sgilt.core.survey.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Type d'une question de sondage, détermine le composant de rendu front et la forme attendue
 * de la réponse dans {@code answers} à la soumission.
 */
public enum QuestionType {

    @JsonProperty("single_choice")
    SINGLE_CHOICE,

    @JsonProperty("multi_choice")
    MULTI_CHOICE,

    @JsonProperty("open_text")
    OPEN_TEXT,

    @JsonProperty("email")
    EMAIL
}
