package net.franzka.sgilt.core.ficheia.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.ficheia.api.FicheIaApi;
import net.franzka.sgilt.core.ficheia.dto.FicheIaApplyRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.service.FicheIaApplyService;
import net.franzka.sgilt.core.ficheia.service.FicheIaOrchestrationService;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
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
    private final FicheIaApplyService ficheIaApplyService;
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

    /**
     * Applique une instruction (section + action) issue de la dernière génération IA à la fiche du
     * prestataire connecté. Les valeurs appliquées sont lues côté serveur, jamais transmises dans
     * la requête. Réservé au prestataire connecté (ROLE_PRO).
     *
     * @param request l'instruction (section + action, ou action seule pour ECRASER_TOUT)
     * @return la fiche complète après application
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_PRO')")
    public ResponseEntity<PrestataireDetailDto> apply(FicheIaApplyRequest request) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("PATCH /prestataires/ma-fiche/generation-ia — email={}, section={}, action={}",
                utilisateur.getEmail(), request.section(), request.action());
        return ResponseEntity.ok(ficheIaApplyService.apply(utilisateur, request.section(), request.action()));
    }

    /**
     * Récupère l'état courant de la génération IA du prestataire connecté (résultat généré, quota
     * restant, date de dernière génération), sans créer de ligne en base si aucune génération
     * n'existe encore. Réservé au prestataire connecté (ROLE_PRO).
     *
     * @return l'état courant de la génération IA
     */
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_PRO')")
    public ResponseEntity<FicheIaGenerationResultDto> getState() {
        Utilisateur utilisateur = currentUserService.get();
        log.info("GET /prestataires/ma-fiche/generation-ia — email={}", utilisateur.getEmail());
        return ResponseEntity.ok(ficheIaOrchestrationService.getState(utilisateur));
    }
}
