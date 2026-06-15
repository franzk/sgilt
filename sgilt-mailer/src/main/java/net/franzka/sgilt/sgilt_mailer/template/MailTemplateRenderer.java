package net.franzka.sgilt.sgilt_mailer.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interpole les variables d'un contexte dans un gabarit de mail.
 * Les valeurs sont échappées HTML par défaut, sauf celles explicitement
 * marquées comme HTML-safe dans le gabarit.
 */
@Component
@Slf4j
public class MailTemplateRenderer {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\w+)}");

    /**
     * Vérifie que le contexte fournit toutes les variables requises par le gabarit.
     *
     * @param mailType le type de mail concerné, utilisé pour le message d'erreur
     * @param template le gabarit dont les variables requises doivent être présentes
     * @param context  le contexte de variables fourni par l'appelant
     * @throws MissingTemplateVariableException si une variable requise est absente ou nulle
     */
    public void validate(MailType mailType, MailTemplate template, Map<String, Object> context) {
        for (String requiredVariable : template.requiredVariables()) {
            if (context.get(requiredVariable) == null) {
                log.error("Mail type {} : variable '{}' manquante dans le contexte, mail non envoyé", mailType, requiredVariable);
                throw new MissingTemplateVariableException(mailType, requiredVariable);
            }
        }
    }

    /**
     * Interpole les variables du contexte dans le gabarit donné.
     * Les placeholders ont la forme {@code {nomVariable}}.
     *
     * @param template          le gabarit à interpoler
     * @param context           le contexte de variables
     * @param htmlSafeVariables les clés de variables dont la valeur ne doit pas être échappée HTML
     * @return le texte avec les placeholders remplacés par les valeurs du contexte
     */
    public String render(String template, Map<String, Object> context, Set<String> htmlSafeVariables) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(template, lastEnd, matcher.start());
            String variableName = matcher.group(1);
            String value = String.valueOf(context.get(variableName));
            result.append(htmlSafeVariables.contains(variableName) ? value : escapeHtml(value));
            lastEnd = matcher.end();
        }
        result.append(template, lastEnd, template.length());

        return result.toString();
    }

    // échappe les caractères HTML pour éviter qu'une valeur utilisateur ne casse le rendu ou n'injecte du HTML
    private String escapeHtml(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
