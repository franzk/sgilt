package net.franzka.sgilt.sgilt_mailer.template;

/**
 * Levée lorsque le contexte fourni pour un mail ne contient pas
 * une variable requise par le gabarit du {@link MailType} correspondant.
 * Le mail n'est alors pas envoyé.
 */
public class MissingTemplateVariableException extends RuntimeException {

    public MissingTemplateVariableException(MailType mailType, String variableName) {
        super("Missing required variable '" + variableName + "' for mail type " + mailType);
    }
}
