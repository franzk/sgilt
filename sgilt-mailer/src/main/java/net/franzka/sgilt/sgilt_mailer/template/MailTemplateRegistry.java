package net.franzka.sgilt.sgilt_mailer.template;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registre des gabarits de mail, indexés par {@link MailType}.
 * Chaque gabarit est défini dans son propre fichier JSON
 * ({@code resources/mailtemplates/<MailType>.json}), de façon à pouvoir
 * migrer plus tard vers un moteur de template sans changer le contrat d'appel.
 */
@Component
public class MailTemplateRegistry {

    private static final String TEMPLATES_PATH = "mailtemplates/";

    private final Map<MailType, MailTemplate> templates;

    public MailTemplateRegistry(ObjectMapper objectMapper) {
        this.templates = Arrays.stream(MailType.values())
                .collect(Collectors.toMap(Function.identity(), mailType -> loadTemplate(objectMapper, mailType)));
    }

    // charge le gabarit JSON associé au type de mail depuis le classpath
    private MailTemplate loadTemplate(ObjectMapper objectMapper, MailType mailType) {
        String resourcePath = TEMPLATES_PATH + mailType.name() + ".json";
        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
            return objectMapper.readValue(input, MailTemplate.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load mail template " + resourcePath, e);
        }
    }

    /**
     * Retourne le gabarit associé au type de mail donné.
     *
     * @param mailType le type de mail
     * @return le gabarit correspondant
     */
    public MailTemplate getTemplate(MailType mailType) {
        return templates.get(mailType);
    }
}
