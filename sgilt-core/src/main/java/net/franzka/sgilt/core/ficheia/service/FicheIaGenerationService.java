package net.franzka.sgilt.core.ficheia.service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseCreateParams;
import com.openai.models.responses.WebSearchTool;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationSchema;
import net.franzka.sgilt.core.ficheia.dto.PracticalInformationDto;
import net.franzka.sgilt.core.prestataire.dto.FaqItemDto;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import net.franzka.sgilt.core.prestataire.dto.TestimonialDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Service de génération IA du contenu d'une fiche prestataire à partir de l'URL de son site,
 * via la Responses API OpenAI (web search + structured output).
 */
@Service
@Slf4j
public class FicheIaGenerationService {

    private static final String URL_PLACEHOLDER = "{url}";

    private final OpenAIClient openAIClient;
    private final String promptTemplate;

    public FicheIaGenerationService(
            OpenAIClient openAIClient,
            @Value("classpath:ficheia/fiche-ia-generation-prompt.txt") Resource promptResource
    ) {
        this.openAIClient = openAIClient;
        this.promptTemplate = readPromptTemplate(promptResource);
    }

    private static String readPromptTemplate(Resource promptResource) {
        try {
            return promptResource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de charger le prompt de génération IA de fiche", e);
        }
    }

    /**
     * Génère une proposition de contenu de fiche prestataire à partir de l'URL de son site.
     *
     * @param url l'URL du site du prestataire
     * @return le contenu de fiche généré, aligné sur les champs de l'entité Prestataire
     * @throws IllegalStateException si OpenAI ne retourne aucune sortie structurée exploitable
     */
    public FicheIaGenerationDto generate(String url) {
        log.info("Génération IA de fiche à partir de l'URL {}", url);

        StructuredResponseCreateParams<FicheIaGenerationSchema> params = ResponseCreateParams.builder()
                .input(promptTemplate.replace(URL_PLACEHOLDER, url))
                .model(ChatModel.GPT_5_1)
                .addTool(WebSearchTool.builder().type(WebSearchTool.Type.WEB_SEARCH).build())
                .text(FicheIaGenerationSchema.class)
                .build();

        StructuredResponse<FicheIaGenerationSchema> response = openAIClient.responses().create(params);

        FicheIaGenerationSchema schema = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune sortie structurée reçue d'OpenAI pour " + url));

        return toDto(schema);
    }

    private FicheIaGenerationDto toDto(FicheIaGenerationSchema schema) {
        return new FicheIaGenerationDto(
                schema.shortDescription,
                schema.baseline,
                schema.offerings,
                new IdentityDto(schema.identity.quote, schema.identity.bio),
                schema.testimonials.stream()
                        .map(testimonial -> new TestimonialDto(testimonial.author, testimonial.text))
                        .toList(),
                schema.practicalInformations.stream()
                        .map(info -> new PracticalInformationDto(info.category, info.title, info.description))
                        .toList(),
                schema.faq.stream()
                        .map(faq -> new FaqItemDto(faq.question, faq.answer))
                        .toList(),
                schema.budget
        );
    }
}
