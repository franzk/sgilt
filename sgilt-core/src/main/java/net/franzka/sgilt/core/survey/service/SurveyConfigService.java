package net.franzka.sgilt.core.survey.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.survey.domain.Survey;
import net.franzka.sgilt.core.survey.exception.SurveyNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Charge et met en cache la configuration JSON statique d'un sondage
 * ({@code classpath:survey/{slug}.json}) — source unique réutilisée pour servir la config au front
 * et pour valider les réponses soumises à la soumission.
 */
@Service
@RequiredArgsConstructor
public class SurveyConfigService {

    private static final String RESOURCE_PATH_TEMPLATE = "classpath:survey/%s.json";

    private final ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Survey> cache = new ConcurrentHashMap<>();

    /**
     * Retourne la configuration du sondage correspondant au slug donné, en cache après le premier
     * chargement.
     *
     * @param slug identifiant du sondage
     * @return la configuration chargée depuis {@code survey/{slug}.json}
     * @throws SurveyNotFoundException si aucun fichier de config ne correspond à ce slug
     */
    public Survey getBySlug(String slug) {
        Survey cached = cache.get(slug);
        if (cached != null) {
            return cached;
        }
        Survey loaded = load(slug);
        cache.put(slug, loaded);
        return loaded;
    }

    private Survey load(String slug) {
        Resource resource = resourceLoader.getResource(RESOURCE_PATH_TEMPLATE.formatted(slug));
        if (!resource.exists()) {
            throw new SurveyNotFoundException(slug);
        }
        try {
            return objectMapper.readValue(resource.getInputStream(), Survey.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de charger la config du sondage " + slug, e);
        }
    }
}
