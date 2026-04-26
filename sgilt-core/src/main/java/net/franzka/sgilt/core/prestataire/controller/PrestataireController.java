package net.franzka.sgilt.core.prestataire.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.api.PrestataireApi;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireSearchResponseDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller HTTP pour la consultation des prestataires.
 * Endpoints publics : aucune authentification requise.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PrestataireController implements PrestataireApi {

    private final PrestataireService prestataireService;

    /**
     * Recherche des prestataires avec filtres optionnels.
     *
     * @param categoryKey clé de catégorie (null = toutes)
     * @param subcatKey   liste de clés de sous-catégorie (null ou vide = toutes)
     * @return réponse avec résultats et compteurs
     */
    @Override
    @Transactional
    public ResponseEntity<PrestataireSearchResponseDto> search(String categoryKey, List<String> subcatKey) {
        log.info("GET /prestataires — categoryKey={}", categoryKey);
        return ResponseEntity.ok(prestataireService.search(categoryKey, subcatKey));
    }

    /**
     * Charge la fiche complète d'un prestataire par son slug.
     *
     * @param slug le slug du prestataire
     * @return la fiche complète, ou 404
     */
    @Override
    @Transactional
    public ResponseEntity<PrestataireDetailDto> getBySlug(String slug) {
        log.info("GET /prestataires/{}", slug);
        return ResponseEntity.ok(prestataireService.getBySlug(slug));
    }
}
