package net.franzka.sgilt.core.prestataire.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.api.CategorieApi;
import net.franzka.sgilt.core.prestataire.domain.MoveDirection;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieCreateRequest;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieUpdateRequest;
import net.franzka.sgilt.core.prestataire.service.CategorieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller HTTP de l'administration des catégories et sous-catégories prestataire
 * (rôle {@code ROLE_ADMIN}).
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CategorieController implements CategorieApi {

    private final CategorieService categorieService;

    /**
     * Liste les catégories et leurs sous-catégories pour le back-office.
     *
     * @return les catégories, avec le nombre de prestataires par sous-catégorie
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CategorieAdminDto>> listCategoriesAdmin() {
        log.info("GET /admin/categories");
        return ResponseEntity.ok(categorieService.getCategoriesAdmin());
    }

    /**
     * Crée une sous-catégorie, en dernière position de sa catégorie.
     *
     * @param request la clé, le libellé et la catégorie
     * @return 201 Created avec la sous-catégorie créée
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SousCategorieAdminDto> createSousCategorie(SousCategorieCreateRequest request) {
        log.info("POST /admin/sous-categories — key={}", request.key());
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.createSousCategorie(request));
    }

    /**
     * Modifie le libellé et la catégorie d'une sous-catégorie.
     *
     * @param key     la clé de la sous-catégorie
     * @param request le libellé et la catégorie
     * @return la sous-catégorie modifiée
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SousCategorieAdminDto> updateSousCategorie(String key, SousCategorieUpdateRequest request) {
        log.info("PATCH /admin/sous-categories/{}", key);
        return ResponseEntity.ok(categorieService.updateSousCategorie(key, request));
    }

    /**
     * Déplace une sous-catégorie d'un rang dans l'ordre d'affichage de sa catégorie.
     *
     * @param key       la clé de la sous-catégorie
     * @param direction le sens du déplacement
     * @return la sous-catégorie déplacée
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SousCategorieAdminDto> moveSousCategorie(String key, MoveDirection direction) {
        log.info("POST /admin/sous-categories/{}/move — direction={}", key, direction);
        return ResponseEntity.ok(categorieService.moveSousCategorie(key, direction));
    }

    /**
     * Supprime une sous-catégorie qu'aucun prestataire n'utilise.
     *
     * @param key la clé de la sous-catégorie
     * @return 204 No Content
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSousCategorie(String key) {
        log.info("DELETE /admin/sous-categories/{}", key);
        categorieService.deleteSousCategorie(key);
        return ResponseEntity.noContent().build();
    }
}
