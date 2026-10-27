package net.franzka.sgilt.core.evenement.page.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.page.api.EventPageApi;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP des pages d'événement. Endpoint public, sans authentification : la page
 * /e/{slug} est ouverte à tous.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class EventPageController implements EventPageApi {

    private final PageEvenementService pageEvenementService;

    /**
     * Retourne la page publique d'un événement.
     *
     * @param slug le slug de la page
     * @return la page publique, ou 404 si le slug est inconnu
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventPageDto> getEventPage(String slug) {
        log.info("GET /eventpages/{}", slug);
        return ResponseEntity.ok(pageEvenementService.getPublicPage(slug));
    }
}
