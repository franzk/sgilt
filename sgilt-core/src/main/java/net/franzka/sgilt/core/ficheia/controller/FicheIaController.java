package net.franzka.sgilt.core.ficheia.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.ficheia.api.FicheIaApi;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.service.FicheIaOrchestrationService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP pour la génération IA de fiche du prestataire connecté.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class FicheIaController implements FicheIaApi {

    private final FicheIaOrchestrationService ficheIaOrchestrationService;
    private final CurrentUserService currentUserService;

    /**
     * Lance une génération IA de fiche à partir de l'URL du site du prestataire connecté.
     * Réservé au prestataire connecté (ROLE_PRO).
     *
     * @param request l'URL du site à analyser
     * @return le résultat généré et le quota restant
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_PRO')")
    public ResponseEntity<FicheIaGenerationResultDto> generate(FicheIaGenerationRequest request) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("POST /prestataires/ma-fiche/generation-ia — email={}", utilisateur.getEmail());
        return ResponseEntity.ok(ficheIaOrchestrationService.generate(utilisateur, request.url()));
    }
}
