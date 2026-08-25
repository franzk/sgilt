package net.franzka.sgilt.core.survey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.survey.api.SurveyApi;
import net.franzka.sgilt.core.survey.domain.Survey;
import net.franzka.sgilt.core.survey.service.SurveyConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP public (aucune authentification) pour la configuration des sondages.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SurveyController implements SurveyApi {

    private final SurveyConfigService surveyConfigService;

    /**
     * Retourne la configuration du sondage correspondant au slug donné, pour que le front construise
     * le formulaire.
     *
     * @param slug identifiant du sondage
     * @return la configuration du sondage
     */
    @Override
    public ResponseEntity<Survey> getConfig(String slug) {
        log.info("GET /surveys/{}", slug);
        return ResponseEntity.ok(surveyConfigService.getBySlug(slug));
    }
}
