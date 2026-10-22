package net.franzka.sgilt.sgilt_mailer.template;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cohérence structurelle des gabarits de mail, pour chaque {@link MailType} : le gabarit existe et
 * se charge, et ses placeholders correspondent aux variables qu'il déclare. Ne fige aucun texte :
 * reformuler un mail ne demande pas de toucher à ce test.
 */
class MailTemplateConsistencyTest {

    // Même forme de placeholder que MailTemplateRenderer : {nomVariable}
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\w+)}");

    private final MailTemplateRegistry registry = new MailTemplateRegistry(new ObjectMapper());

    @ParameterizedTest
    @EnumSource(MailType.class)
    void givenMailType_whenGetTemplate_thenSubjectAndHtmlAreDefined(MailType mailType) {
        MailTemplate template = registry.getTemplate(mailType);

        assertThat(template).isNotNull();
        assertThat(template.subjectTemplate()).isNotBlank();
        assertThat(template.htmlTemplate()).isNotBlank();
    }

    @ParameterizedTest
    @EnumSource(MailType.class)
    void givenMailType_whenReadPlaceholders_thenTheyMatchRequiredVariables(MailType mailType) {
        MailTemplate template = registry.getTemplate(mailType);

        assertThat(placeholders(template.subjectTemplate() + template.htmlTemplate()))
                .as("placeholders de %s", mailType)
                .isEqualTo(template.requiredVariables());
    }

    @ParameterizedTest
    @EnumSource(MailType.class)
    void givenMailType_whenReadHtmlSafeVariables_thenTheyAreRequiredVariables(MailType mailType) {
        MailTemplate template = registry.getTemplate(mailType);

        assertThat(template.requiredVariables())
                .as("htmlSafeVariables de %s", mailType)
                .containsAll(template.htmlSafeVariables());
    }

    // les noms de variables utilisés dans un gabarit
    private static Set<String> placeholders(String template) {
        return PLACEHOLDER_PATTERN.matcher(template).results()
                .map(match -> match.group(1))
                .collect(Collectors.toSet());
    }
}
