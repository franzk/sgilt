package net.franzka.sgilt.core.prestataire.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.api.PrestataireApi;
import net.franzka.sgilt.core.prestataire.domain.Engagement;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireSearchResponseDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireUpdateDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controller HTTP pour la consultation des prestataires.
 * Endpoints publics : aucune authentification requise.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PrestataireController implements PrestataireApi {

    private final PrestataireService prestataireService;
    private final CurrentUserService currentUserService;

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

    /**
     * Retourne les clés de l'enum {@code Engagement}.
     * Réservé à l'édition de fiche — non exposé sur le chemin public/display.
     *
     * @return liste ordonnée des noms d'enum
     */
    @Override
    public ResponseEntity<List<String>> getEngagementKeys() {
        log.info("GET /prestataires/engagements");
        return ResponseEntity.ok(
                Arrays.stream(Engagement.values())
                        .map(Enum::name)
                        .toList()
        );
    }

    /**
     * Met à jour la fiche d'un prestataire.
     * Réservé au propriétaire de la fiche (ROLE_PRO).
     *
     * @param id  identifiant du prestataire à modifier
     * @param dto les champs à mettre à jour (null = non modifié)
     * @return 204 No Content
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_PRO')")
    public ResponseEntity<Void> update(UUID id, PrestataireUpdateDto dto) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("PATCH /prestataires/{} — email={}", id, utilisateur.getEmail());
        prestataireService.update(id, dto, utilisateur);
        return ResponseEntity.noContent().build();
    }
}
