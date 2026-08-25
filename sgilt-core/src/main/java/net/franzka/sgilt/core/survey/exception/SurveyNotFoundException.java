package net.franzka.sgilt.core.survey.exception;

public class SurveyNotFoundException extends RuntimeException {
    public SurveyNotFoundException(String slug) {
        super("Sondage introuvable : " + slug);
    }
}
