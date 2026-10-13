package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementRubrique;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.CreateEventRequest;
import net.franzka.sgilt.core.evenement.dto.CreateEventResponse;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.EventDto;
import net.franzka.sgilt.core.evenement.dto.EventMetaDto;
import net.franzka.sgilt.core.evenement.dto.EventMetaPatchDto;
import net.franzka.sgilt.core.evenement.dto.EventRubriqueDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.template.service.TemplateService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private final FileStorageService fileStorageService;
    private final TemplateService templateService;

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
     * Retourne tout un événement pour l'EventBoard : ses métadonnées et ses rubriques, chacune
     * avec ses réservations.
     *
     * @param eventId       l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return l'événement complet
     * @throws EvenementNotFoundException   si l'événement n'existe pas
     * @throws EvenementNotAllowedException si l'événement n'appartient pas à l'utilisateur connecté
     */
    public EventDto getEvent(UUID eventId, UUID utilisateurId) {
        Evenement event = getOwnedEvent(eventId, utilisateurId);
        LocalDateTime lastUpdateDate = journalEvenementService.derniereModification(eventId).orElse(null);
        EventMetaDto meta = evenementMapper.toMetaDto(event, lastUpdateDate);
        return new EventDto(meta, rubriquesWithReservations(event));
    }

    // Rubriques de l'événement, dans son ordre, chacune avec les réservations qui y sont rangées
    // (rubrique fixée à l'entrée de chaque réservation).
    private List<EventRubriqueDto> rubriquesWithReservations(Evenement event) {
        // 1 — les réservations de l'événement, regroupées par rubrique.
        Map<String, List<ReservationSummaryDto>> reservationsByRubrique = new HashMap<>();
        for (ReservationSummaryDto reservation : reservationService.getReservationSummaries(event.getId())) {
            reservationsByRubrique.computeIfAbsent(reservation.rubriqueKey(), key -> new ArrayList<>()).add(reservation);
        }

        // 2 — chaque rubrique de l'événement, dans son ordre, avec ses réservations (aucune si vide).
        List<EventRubriqueDto> rubriques = new ArrayList<>();
        for (EvenementRubrique rubrique : event.getRubriques()) {
            rubriques.add(new EventRubriqueDto(rubrique.key(), reservationsByRubrique.getOrDefault(rubrique.key(), List.of())));
        }
        return rubriques;
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
        Evenement event = getOwnedEvent(eventId, utilisateur.getId());
        Prestataire prestataire = prestataireService.getById(prestataireId);
        String rubriqueKey = templateService.rubriqueOf(event.getEventType(), prestataire.getSubcatKey());
        addRubrique(event, rubriqueKey); // si la catégorie du la sous catégorie du prestataire n'a pas de rubrique correspondante dans l'événement, on l'ajoute à la fin de la liste des rubriques
        reservationService.create(event, prestataire, utilisateur, event.getDate(), message, rubriqueKey);
    }

    // Ajoute à l'événement la rubrique où entre une réservation, si elle n'y est pas encore, à la
    // position que lui donne le template. Les rubriques déjà présentes restent telles quelles.
    private void addRubrique(Evenement event, String rubriqueKey) {
        List<String> keys = event.getRubriques().stream().map(EvenementRubrique::key).toList();
        if (keys.contains(rubriqueKey)) return;

        int position = templateService.insertionPosition(event.getEventType(), keys, rubriqueKey);
        List<EvenementRubrique> rubriques = new ArrayList<>(event.getRubriques());
        rubriques.add(position, new EvenementRubrique(rubriqueKey));
        event.setRubriques(rubriques);
        evenementRepository.save(event);
    }

    /**
     * Crée un événement pour un utilisateur authentifié, avec sa première demande s'il en a une
     * (prestataire vérifié publié, réservation rangée dans sa rubrique).
     *
     * @param utilisateur l'utilisateur connecté (propriétaire de l'événement)
     * @param request     les données de l'événement et sa première demande éventuelle
     * @return l'identifiant du nouvel événement
     * @throws PrestataireNotFoundException si le prestataire de la demande n'existe pas ou n'est pas publié
     */
    public CreateEventResponse createEvent(Utilisateur utilisateur, CreateEventRequest request) {
        if (request.demande() != null) prestataireService.ensurePublished(request.demande().prestataireId());
        return new CreateEventResponse(createFromDto(utilisateur, buildEvenement(request)).getId());
    }

    /**
     * Construit, sans l'enregistrer, un événement à créer : ses données, les rubriques du template
     * de son type et, s'il naît d'une demande, la demande rangée dans sa rubrique. Commun à la
     * création connectée et à l'onboarding (où l'événement attend l'activation du compte).
     *
     * @param request les données de l'événement et sa première demande éventuelle
     * @return l'événement complet, prêt pour {@link #createFromDto}
     */
    public EvenementDto buildEvenement(CreateEventRequest request) {
        List<RubriqueDto> rubriques = request.demande() == null
                ? templateService.getNewEventRubriques(request.eventType())
                : templateService.getEventRubriquesFromDemande(request.eventType(), request.demande());
        return new EvenementDto(
                request.title(), request.eventType(), request.ambiance(), request.momentCle(), request.description(),
                request.date(), request.ville(), request.nbInvites(), request.lieu(), rubriques);
    }

    /**
     * Crée en statut ACTIVE un événement complet (données et rubriques), puis une réservation
     * pour chaque demande initiée rangée dans ses rubriques. Sans nom choisi, l'événement reçoit un
     * nom par défaut tiré de sa date. Seules les clés des rubriques sont
     * enregistrées sur l'événement : les demandes deviennent des réservations.
     *
     * @param utilisateur l'utilisateur propriétaire de l'événement
     * @param evenement   l'événement : données, rubriques et demandes initiées
     * @return l'événement sauvegardé
     */
    public Evenement createFromDto(Utilisateur utilisateur, EvenementDto evenement) {
        Evenement created = evenementMapper.toEvenement(evenement, utilisateur);
        created.setTitle(StringUtils.hasText(evenement.title()) ? evenement.title() : defaultName(evenement.date()));
        created = evenementRepository.save(created);

        createReservationsFromDemandesInitiees(created, evenement.rubriques());
        return created;
    }

    /**
     * Crée une réservation pour chaque demande initiée rangée dans les rubriques d'un événement
     * qui vient d'être créé, au nom de son propriétaire et pour sa date. Chaque réservation garde
     * la rubrique de sa demande.
     *
     * @param evenement l'événement créé
     * @param rubriques ses rubriques telles que transmises, avec leurs demandes initiées
     */
    private void createReservationsFromDemandesInitiees(Evenement evenement, List<RubriqueDto> rubriques) {
        if (rubriques == null) return;
        rubriques.forEach(rubrique -> Objects.requireNonNullElse(rubrique.reservations(), List.<DemandeInitieeDto>of())
                .forEach(demande -> reservationService.create(evenement,
                        prestataireService.getById(demande.prestataireId()),
                        evenement.getUtilisateur(), evenement.getDate(), demande.message(), rubrique.key())));
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
    public EventMetaDto patchEvent(UUID eventId, UUID utilisateurId, EventMetaPatchDto patch) {
        Evenement event = getOwnedEvent(eventId, utilisateurId);
        List<ModificationChamp> modifications = computeModifications(event, patch);
        applyPatch(event, patch);
        evenementRepository.save(event);
        journalEvenementService.save(event, modifications);
        LocalDateTime lastUpdateDate = journalEvenementService.derniereModification(eventId).orElse(null);
        return evenementMapper.toMetaDto(event, lastUpdateDate);
    }

    private List<ModificationChamp> computeModifications(Evenement event, EventMetaPatchDto patch) {
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

    private void applyPatch(Evenement event, EventMetaPatchDto patch) {
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
     * @throws FileStorageException        en cas d'erreur de stockage
     */
    public CoverUrlDto updateCover(UUID eventId, UUID utilisateurId, MultipartFile file) {
        Evenement event = getOwnedEvent(eventId, utilisateurId);
        supprimerAncienneImageSiDeletable(event.getImagePath());
        try {
            String imagePath = fileStorageService.upload(file, "uploads");
            event.setImagePath(imagePath);
            evenementRepository.save(event);
            return new CoverUrlDto(imagePath);
        } catch (IOException e) {
            throw new FileStorageException("Erreur de stockage de l'image pour l'événement " + eventId, e);
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
        Evenement event = getOwnedEvent(eventId, utilisateurId);
        supprimerAncienneImageSiDeletable(event.getImagePath());
        event.setImagePath(imagePath);
        evenementRepository.save(event);
        return new CoverUrlDto(imagePath);
    }

    private void supprimerAncienneImageSiDeletable(String imagePath) {
        if (imagePath == null || isProtectedImagePath(imagePath)) return;
        try {
            fileStorageService.delete(imagePath);
        } catch (IOException e) {
            throw new FileStorageException("Erreur de suppression de l'ancienne image", e);
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
    private Evenement getOwnedEvent(UUID eventId, UUID utilisateurId) {
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

    private String defaultName(LocalDate date) {
        if (date == null) return "Mon événement";
        String month = date.format(DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH));
        return "Mon événement du " + date.getDayOfMonth() + " " + capitalize(month);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
