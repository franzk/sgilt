package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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
     * Retourne la liste des événements de l'utilisateur identifié par son id.
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
     * Retourne les métadonnées d'un événement pour l'EventBoard.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return les métadonnées de l'événement
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur connecté
     */
    public EventDetailDto getEventDetail(UUID eventId, UUID utilisateurId) {
        Evenement event = getEvent(eventId, utilisateurId);
        return evenementMapper.toDetailDto(event, computeCountdown(event.getDate()));
    }

    /**
     * Retourne les compteurs de réservations et le mood pour l'EventBoard.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return les compteurs par statut et le mood calculé
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur connecté
     */
    public EventCountsDto getEventCounts(UUID eventId, UUID utilisateurId) {
        getEvent(eventId, utilisateurId); // vérifie que l'événement appartient bien à l'utilisateur
        return buildEventCounts(reservationService.getStatusCountsByEvenement(eventId));
    }

    /**
     * Retourne la liste des réservations d'un événement pour l'EventBoard.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return la liste des résumés de réservations
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur connecté
     */
    public List<ReservationSummaryDto> getEventReservations(UUID eventId, UUID utilisateurId) {
        getEvent(eventId, utilisateurId); // vérifie que l'événement appartient bien à l'utilisateur
        return reservationService.getReservationSummaries(eventId);
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

    /**
     * Récupère un événement par son id et vérifie qu'il appartient à l'utilisateur donné.
     *
     * @param eventId id de l'événement
     * @param utilisateurId id de l'utilisateur
     * @return l'événement si trouvé et autorisé
     * @throws EvenementNotFoundException si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur
     */
    private Evenement getEvent(UUID eventId, UUID utilisateurId) {
        Evenement event = evenementRepository.findById(eventId)
                .orElseThrow(EvenementNotFoundException::new);
        if (!event.getUtilisateur().getId().equals(utilisateurId)) {
            throw new EvenementNotAllowedException();
        }
        return event;
    }

    /**
     * Mappe les comptes par statuts d'un événement en un DTO de compteurs et mood pour l'EventBoard.
     *
     * @param counts les comptes par statut de réservation pour un événement
     * @return un DTO contenant les compteurs et le mood calculé
     */
    private EventCountsDto buildEventCounts(Map<ReservationStatus, Integer> counts) {
        int confirmed    = counts.getOrDefault(ReservationStatus.CONFIRMED, 0);
        int inDiscussion = counts.getOrDefault(ReservationStatus.IN_DISCUSSION, 0);
        int nouvelle     = counts.getOrDefault(ReservationStatus.NEW, 0);
        int refusee      = counts.getOrDefault(ReservationStatus.REFUSED_PRE_CONTACT, 0)
                         + counts.getOrDefault(ReservationStatus.REFUSED_POST_CONTACT, 0);
        int annulee      = counts.getOrDefault(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT, 0)
                         + counts.getOrDefault(ReservationStatus.CANCELED_BY_CLIENT_POST_CONTACT, 0)
                         + counts.getOrDefault(ReservationStatus.CANCELED_POST_CONFIRMATION, 0);
        int realisee     = counts.getOrDefault(ReservationStatus.DONE, 0);

        return new EventCountsDto(computeMood(confirmed, inDiscussion, nouvelle),
                confirmed, inDiscussion, nouvelle, refusee, annulee, realisee);
    }

    /**
     * calcule le mood pour l'EventBoard.
     *
     * @param confirmed nombre de réservations confirmées
     * @param inDiscussion nombre de réservations en discussion
     * @param nouvelle nombre de réservations nouvelles en attente de recontact par le prestataire
     * @return le mood calculé : "confirmee", "en_discussion", "nouvelle" ou "defaut"
     */
    private String computeMood(int confirmed, int inDiscussion, int nouvelle) {
        int active = confirmed + inDiscussion + nouvelle;
        if (active == 0)                         return "defaut";
        if (confirmed > inDiscussion + nouvelle) return "confirmee";
        if (inDiscussion > 0)                    return "en_discussion";
        return "nouvelle";
    }

    /**
     * Calcule le seuil d'imminence de l'arrivée de l'événement pour l'EventBoard.
     *
     * @param eventDate date de l'événement
     * @return le seuil calculé : serein, past, imminent, proche, ou defaut
     */
    private String computeCountdown(LocalDate eventDate) {
        if (eventDate == null) return "serein";
        long days = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);
        if (days < 0)  return "past";
        if (days < 30) return "imminent";
        if (days < 90) return "proche";
        return "serein";
    }
}
