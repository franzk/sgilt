package net.franzka.sgilt.core.template.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.template.api.TemplateApi;
import net.franzka.sgilt.core.template.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller HTTP des templates d'événement. Endpoints publics et sans état : le parcours public,
 * dont l'événement vit dans le navigateur, obtient ici les règles du template sans les dupliquer.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class TemplateController implements TemplateApi {

    private final TemplateService templateService;

    /**
     * Retourne les rubriques d'un nouvel événement du type donné, à copier dans l'événement.
     *
     * @param eventType le type d'événement
     * @return les rubriques, vides, dans l'ordre d'affichage (vide sans template)
     */
    @Override
    @Transactional
    public ResponseEntity<List<RubriqueDto>> getNewEventRubriques(String eventType) {
        log.info("GET /templates/{}/rubriques", eventType);
        return ResponseEntity.ok(templateService.getNewEventRubriques(eventType));
    }
}
