package net.franzka.sgilt.core.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.mailer.AdminMailerService;
import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.jwt.service.ActionTokenService;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.PrestataireOnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service métier pour le suivi et la relance des onboardings prestataire en attente
 * (lien envoyé par email, pas encore cliqué) depuis le back-office admin.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminOnboardingService {

    private final ActionTokenService actionTokenService;
    private final ActionLinkService actionLinkService;
    private final PrestataireService prestataireService;
    private final PrestataireMapper prestataireMapper;
    private final AdminMailerService adminMailerService;

    /**
     * Liste tous les prestataires dont l'onboarding est en attente — le lien envoyé par email n'a
     * pas encore été cliqué, quel que soit son état d'expiration.
     *
     * @return la liste des onboardings en attente
     */
    public List<PrestataireOnboardingPendingDto> listPendingOnboardings() {
        return actionTokenService.findAllByType(ActionType.PRESTATAIRE_ONBOARDING).stream()
                .map(this::toPendingDto)
                .toList();
    }

    /**
     * Renvoie le mail d'activation à un prestataire dont l'onboarding est en attente, en
     * réinitialisant la période de validité du lien.
     *
     * @param prestataireId identifiant du prestataire dont l'onboarding doit être relancé
     * @return {@code true} si l'envoi a réussi, {@code false} si l'appel au mailer a échoué
     * @throws jakarta.persistence.EntityNotFoundException si aucun onboarding n'est en attente pour ce prestataire
     */
    public boolean resendOnboardingEmail(UUID prestataireId) {
        Prestataire prestataire = prestataireService.getById(prestataireId);
        Utilisateur utilisateur = prestataire.getUtilisateur();

        ActionToken actionToken =
                actionTokenService.findPendingByEmail(ActionType.PRESTATAIRE_ONBOARDING, utilisateur.getEmail());
        String actionUrl = actionLinkService.rebuildLink(actionToken);

        log.info("resendOnboardingEmail — relance de l'onboarding prestataire pour {}", utilisateur.getEmail());
        return adminMailerService.sendPrestataireOnboardingEmail(
                utilisateur.getEmail(), utilisateur.getFirstName(), actionUrl);
    }

    private PrestataireOnboardingPendingDto toPendingDto(ActionToken actionToken) {
        String email = (String) actionTokenService.readPayload(actionToken).get("email");
        Prestataire prestataire = prestataireService.getByUtilisateurEmail(email);

        return prestataireMapper.toOnboardingPendingDto(
                prestataire, actionToken.getCreatedAt(), actionToken.getExpiresAt());
    }
}
