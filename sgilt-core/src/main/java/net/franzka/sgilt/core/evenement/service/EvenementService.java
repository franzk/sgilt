package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.image.ImageStorageException;
import net.franzka.sgilt.core.image.ImageStorageService;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Evenement}.
 */
@Service
@RequiredArgsConstructor
public class EvenementService {

    private static final Set<String> PROTECTED_PREFIXES = Set.of("bank/");

    private final EvenementRepository evenementRepository;
    private final ReservationService reservationService;
    private final PrestataireService prestataireService;
    private final EvenementMapper evenementMapper;
    private final JournalEvenementService journalEvenementService;
    private final ImageStorageService imageStorageService;

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
        LocalDateTime lastUpdateDate = journalEvenementService.derniereModification(eventId).orElse(null);
        return evenementMapper.toDetailDto(event, computeCountdown(event.getDate()), lastUpdateDate);
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
     * Vérifie que l'utilisateur est bien le propriétaire de l'événement.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur connecté
     */
    public void verifyEventOwnership(UUID eventId, UUID utilisateurId) {
        getEvent(eventId, utilisateurId);
    }

    /**
     * Ajoute une réservation à un événement existant.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateur   l'utilisateur connecté (doit être le propriétaire)
     * @param prestataireId l'identifiant du prestataire à contacter
     * @param message       message optionnel à destination du prestataire
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'utilisateur n'est pas le propriétaire
     */
    public void addReservation(UUID eventId, Utilisateur utilisateur, UUID prestataireId, String message) {
        Evenement event = getEvent(eventId, utilisateur.getId());
        Prestataire prestataire = prestataireService.getById(prestataireId);
        reservationService.create(event, prestataire, utilisateur, event.getDate(), message);
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
                .title(defaultName(formData.date()))
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
     * Met à jour les champs modifiables d'un événement.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @param patch         les champs à mettre à jour (null = non modifié)
     * @return les métadonnées mises à jour
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur connecté
     */
    public EventDetailDto patchEvent(UUID eventId, UUID utilisateurId, EventPatchDto patch) {
        Evenement event = getEvent(eventId, utilisateurId);
        List<ModificationChamp> modifications = computeModifications(event, patch);
        applyPatch(event, patch);
        evenementRepository.save(event);
        journalEvenementService.save(event, modifications);
        LocalDateTime lastUpdateDate = journalEvenementService.derniereModification(eventId).orElse(null);
        return evenementMapper.toDetailDto(event, computeCountdown(event.getDate()), lastUpdateDate);
    }

    private List<ModificationChamp> computeModifications(Evenement event, EventPatchDto patch) {
        List<ModificationChamp> modifications = new ArrayList<>();
        if (patch.title()       != null) addIfUpdated(modifications, "titre",        event.getTitle(),       blankToNull(patch.title()));
        if (patch.lieu()        != null) addIfUpdated(modifications, "lieu",         event.getLieu(),        blankToNull(patch.lieu()));
        if (patch.sharedNote()  != null) addIfUpdated(modifications, "notePartagee", event.getNotePartagee(), patch.sharedNote());
        if (patch.eventType()   != null) addIfUpdated(modifications, "eventType",    event.getEventType(),   blankToNull(patch.eventType()));
        if (patch.ambiance()    != null) addIfUpdated(modifications, "ambiance",     event.getAmbiance(),    blankToNull(patch.ambiance()));
        if (patch.ville()       != null) addIfUpdated(modifications, "ville",        event.getVille(),       blankToNull(patch.ville()));
        if (patch.nbInvites()   != null) addIfUpdated(modifications, "nbInvites",    event.getNbInvites(),   blankToNull(patch.nbInvites()));
        if (patch.description() != null) addIfUpdated(modifications, "description",  event.getDescription(), blankToNull(patch.description()));
        if (patch.momentCle()   != null) addIfUpdated(modifications, "momentCle",    event.getMomentCle(),   blankToNull(patch.momentCle()));
        return modifications;
    }

    private void addIfUpdated(List<ModificationChamp> modifications, String champ, String avant, String apres) {
        if (!Objects.equals(avant, apres)) {
            modifications.add(new ModificationChamp(champ, avant, apres));
        }
    }

    private void applyPatch(Evenement event, EventPatchDto patch) {
        if (patch.title()       != null) event.setTitle(blankToNull(patch.title()));
        if (patch.lieu()        != null) event.setLieu(blankToNull(patch.lieu()));
        if (patch.sharedNote()  != null) event.setNotePartagee(patch.sharedNote());
        if (patch.eventType()   != null) event.setEventType(blankToNull(patch.eventType()));
        if (patch.ambiance()    != null) event.setAmbiance(blankToNull(patch.ambiance()));
        if (patch.ville()       != null) event.setVille(blankToNull(patch.ville()));
        if (patch.nbInvites()   != null) event.setNbInvites(blankToNull(patch.nbInvites()));
        if (patch.description() != null) event.setDescription(blankToNull(patch.description()));
        if (patch.momentCle()   != null) event.setMomentCle(blankToNull(patch.momentCle()));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    /**
     * Met à jour la couverture de l'événement. Supprime l'ancienne image si elle existait.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @param file          le nouveau fichier image
     * @return l'URL de la nouvelle image
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'utilisateur n'est pas le propriétaire
     * @throws ImageStorageException        en cas d'erreur de stockage
     */
    public CoverUrlDto updateCover(UUID eventId, UUID utilisateurId, MultipartFile file) {
        Evenement event = getEvent(eventId, utilisateurId);
        supprimerAncienneImageSiDeletable(event.getImagePath());
        try {
            String imagePath = imageStorageService.upload(file);
            event.setImagePath(imagePath);
            evenementRepository.save(event);
            return new CoverUrlDto(imagePath);
        } catch (IOException e) {
            throw new ImageStorageException("Erreur de stockage de l'image pour l'événement " + eventId, e);
        }
    }

    /**
     * Sélectionne une image de la banque comme couverture de l'événement.
     * Supprime l'ancienne image si elle est un upload (non protégée).
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @param imagePath     le chemin de l'image sélectionnée dans la banque
     * @return l'URL de la nouvelle couverture
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'utilisateur n'est pas le propriétaire
     */
    public CoverUrlDto selectCover(UUID eventId, UUID utilisateurId, String imagePath) {
        Evenement event = getEvent(eventId, utilisateurId);
        supprimerAncienneImageSiDeletable(event.getImagePath());
        event.setImagePath(imagePath);
        evenementRepository.save(event);
        return new CoverUrlDto(imagePath);
    }

    private void supprimerAncienneImageSiDeletable(String imagePath) {
        if (imagePath == null || isProtectedImagePath(imagePath)) return;
        try {
            imageStorageService.delete(imagePath);
        } catch (IOException e) {
            throw new ImageStorageException("Erreur de suppression de l'ancienne image", e);
        }
    }

    private static boolean isProtectedImagePath(String imagePath) {
        return PROTECTED_PREFIXES.stream().anyMatch(imagePath::startsWith);
    }

    /**
     * Vérifie que l'utilisateur peut accéder au journal de l'événement.
     * L'accès est accordé au propriétaire de l'événement ou à tout prestataire
     * ayant au moins une réservation sur cet événement.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'utilisateur n'est ni propriétaire ni prestataire autorisé
     */
    public void verifierAccesLectureJournal(UUID eventId, UUID utilisateurId) {
        Evenement event = chargerEvenement(eventId);
        if (event.getUtilisateur().getId().equals(utilisateurId)) return;
        if (reservationService.prestataireAReservationSurEvenement(eventId, utilisateurId)) return;
        throw new EvenementNotAllowedException();
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
        Evenement event = chargerEvenement(eventId);
        if (!event.getUtilisateur().getId().equals(utilisateurId)) {
            throw new EvenementNotAllowedException();
        }
        return event;
    }

    private Evenement chargerEvenement(UUID eventId) {
        return evenementRepository.findById(eventId)
                .orElseThrow(EvenementNotFoundException::new);
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

    private String defaultName(LocalDate date) {
        if (date == null) return "Mon événement";
        String month = date.format(DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH));
        return "Mon événement du " + date.getDayOfMonth() + " " + capitalize(month);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String computeCountdown(LocalDate eventDate) {
        if (eventDate == null) return "serein";
        long days = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);
        if (days < 0)  return "past";
        if (days < 30) return "imminent";
        if (days < 90) return "proche";
        return "serein";
    }
}
