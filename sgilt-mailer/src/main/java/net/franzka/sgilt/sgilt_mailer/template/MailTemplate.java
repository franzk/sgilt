package net.franzka.sgilt.sgilt_mailer.template;

import java.util.Set;

/**
 * Gabarit d'un mail : sujet et corps HTML interpolables, et clés de variables
 * attendues dans le contexte fourni par l'appelant.
 *
 * @param subjectTemplate   le gabarit du sujet, interpolé avec le contexte
 * @param htmlTemplate      le gabarit HTML du corps, interpolé avec le contexte
 * @param requiredVariables les clés de variables que le contexte doit fournir
 * @param htmlSafeVariables les clés de variables dont la valeur ne doit pas être échappée HTML
 */
public record MailTemplate(
        String subjectTemplate,
        String htmlTemplate,
        Set<String> requiredVariables,
        Set<String> htmlSafeVariables
) {}
