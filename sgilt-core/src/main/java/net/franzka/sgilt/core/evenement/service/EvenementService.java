package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Evenement}.
 */
@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final ReservationService reservationService;
    private final EvenementMapper evenementMapper;

    /**
     * Retourne la liste des événements de l'utilisateur identifié par son email.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return la liste des résumés d'événements
     */
    public List<EvenementSummaryDto> getUserEvents(UUID userId) {
        return evenementRepository.findByUtilisateurId(userId).stream()
                .map(e -> evenementMapper.toSummaryDto(e, reservationService.getCountsForEvenement(e.getId())))
                .toList();
    }

    /**
     * Crée et persiste un événement en statut ACTIVE pour l'utilisateur donné.
     *
     * @param utilisateur l'utilisateur propriétaire de l'événement
     * @param formData    les données saisies dans le tunnel d'onboarding
     * @return l'événement sauvegardé
     */
    public Evenement createFromFormData(Utilisateur utilisateur, InitOnboardingRequest formData) {
        Evenement evenement = Evenement.builder()
                .utilisateur(utilisateur)
                .name(formData.eventType() != null ? formData.eventType() : "Mon événement")
                .date(formData.date())
                .status(EvenementStatus.ACTIVE)
                .lieu(formData.lieu())
                .ville(formData.ville())
                .nbInvites(formData.nbInvites())
                .eventType(formData.eventType())
                .ambiance(formData.ambiance())
                .momentCle(formData.momentCle())
                .description(formData.description())
                .build();
        return evenementRepository.save(evenement);
    }
}
