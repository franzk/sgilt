package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.notifier.event.ReservationCreatedEvent;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationItemDto;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.RefuseReservationRequest;
import net.franzka.sgilt.core.reservation.exception.InvalidStateException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotAllowedException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotFoundException;
import net.franzka.sgilt.core.reservation.mapper.ReservationMapper;
import net.franzka.sgilt.core.reservation.repository.NoteRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;



/**
 * Service métier pour l'entité {@link Reservation}.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final NoteRepository noteRepository;
    private final ReservationMapper reservationMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Retourne les demandes actives (NEW et IN_DISCUSSION) de l'utilisateur, triées NEW en premier,
     * ainsi qu'un indicateur de présence d'au moins une réservation CONFIRMED.
     * Utilisé par la home de l'espace client.
     *
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return le wrapper contenant les items et le flag hasConfirmed
     */
    public ActiveReservationsDto getActiveReservations(UUID utilisateurId) {
        List<Reservation> pending = reservationRepository.findByUtilisateurIdAndStatusIn(
                utilisateurId,
                List.of(ReservationStatus.NEW, ReservationStatus.IN_DISCUSSION)
        );
        boolean hasConfirmed = reservationRepository.existsByUtilisateurIdAndStatus(
                utilisateurId,
                ReservationStatus.CONFIRMED
        );
        List<ActiveReservationItemDto> items = pending.stream()
                .sorted(Comparator.comparing(r -> r.getStatus() == ReservationStatus.NEW ? 0 : 1))
                .map(reservationMapper::toActiveItemDto)
                .toList();
        return new ActiveReservationsDto(items, hasConfirmed);
    }

    /**
     * Retourne le nombre de réservations nouvelles, confirmées et en discussion pour un événement.
     * Utilisé par le résumé de la liste des événements.
     *
     * @param evenementId l'identifiant de l'événement
     * @return les compteurs confirmées / en discussion / nouvelles
     */
    public ReservationCounts getCountsForEvenement(UUID evenementId) {
        List<Reservation> reservations = reservationRepository.findByEvenementId(evenementId);
        return new ReservationCounts(
                count(reservations, ReservationStatus.CONFIRMED),
                count(reservations, ReservationStatus.IN_DISCUSSION),
                count(reservations, ReservationStatus.NEW)
        );
    }

    /**
     * Retourne le nombre de réservations par statut pour un événement.
     * Données brutes — la construction du DTO et le calcul du mood sont à la charge de l'appelant.
     *
     * @param evenementId l'identifiant de l'événement
     * @return une map statut → nombre de réservations
     */
    public Map<ReservationStatus, Integer> getStatusCountsByEvenement(UUID evenementId) {
        List<Reservation> reservations = reservationRepository.findByEvenementId(evenementId);
        return reservations.stream()
            .collect(
                    Collectors.groupingBy(
                            Reservation::getStatus, Collectors.summingInt(r -> 1)));
    }

    /**
     * Retourne les résumés de réservations pour l'EventBoard.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la liste des résumés de réservations
     */
    public List<ReservationSummaryDto> getReservationSummaries(UUID evenementId) {
        return reservationRepository.findByEvenementId(evenementId).stream()
                .map(reservationMapper::toSummaryDto)
                .toList();
    }

    /**
     * Crée et persiste une réservation en statut NEW,
     * accompagnée d'une note initiale contenant le message du client.
     *
     * @param evenement      l'événement associé
     * @param prestataire    le prestataire ciblé
     * @param utilisateur    l'utilisateur à l'origine de la réservation
     * @param date           la date souhaitée pour la prestation
     * @param initialMessage le message initial adressé au prestataire (peut être null)
     * @return la réservation sauvegardée
     */
    public Reservation create(Evenement evenement,
                              Prestataire prestataire,
                              Utilisateur utilisateur,
                              LocalDate date,
                              String initialMessage
    ) {
        Reservation reservation = Reservation.builder()
                .evenement(evenement)
                .prestataire(prestataire)
                .utilisateur(utilisateur)
                .date(date)
                .status(ReservationStatus.NEW)
                .build();
        reservation = reservationRepository.save(reservation);

        Note note = Note.builder()
                .reservation(reservation)
                .utilisateur(utilisateur)
                .title("Demande de réservation")
                .isPersonal(false)
                .content(initialMessage)
                .build();
        noteRepository.save(note);

        applicationEventPublisher.publishEvent(new ReservationCreatedEvent(
                reservation.getId(),
                prestataire.getUtilisateur().getId(),
                prestataire.getUtilisateur().getEmail(),
                utilisateur.getFirstName(),
                utilisateur.getLastName(),
                evenement.getTitle(),
                date
        ));

        return reservation;
    }

    /**
     * Vérifie si l'utilisateur est un prestataire ayant au moins une réservation sur l'événement.
     *
     * @param evenementId   l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @return true si une réservation existe
     */
    public boolean prestataireAReservationSurEvenement(UUID evenementId, UUID utilisateurId) {
        return reservationRepository.existsByEvenementIdAndPrestataireUtilisateurId(evenementId, utilisateurId);
    }

    /**
     * Retourne les métadonnées d'une réservation.
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @return les métadonnées de la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     */
    public ReservationMetaDto getMeta(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        return reservationMapper.toReservationMetaDto(reservation);
    }

    /**
     * Annule une réservation. Transitions valides :
     * - NEW → CANCELED_BY_CLIENT_PRE_CONTACT,
     * - IN_DISCUSSION → CANCELED_BY_CLIENT_POST_CONTACT.
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     * @throws InvalidStateException        si le statut ne permet pas l'annulation
     */
    public void cancel(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        ReservationStatus cancelled = switch (reservation.getStatus()) {
            case NEW           -> ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT;
            case IN_DISCUSSION -> ReservationStatus.CANCELED_BY_CLIENT_POST_CONTACT;
            default -> throw new InvalidStateException(
                    "La réservation ne peut pas être annulée depuis le statut " + reservation.getStatus());
        };
        reservation.setStatus(cancelled);
        // save reservation
        reservationRepository.save(reservation);

        // crée une note système pour alimenter le feed de la réservation, visible par tout le monde
        noteRepository.save(Note.builder()
                .reservation(reservation)
                .utilisateur(reservation.getUtilisateur())
                .generatedKey("feed.system.cancelled")
                .isPersonal(false)
                .build());
    }

    /**
     * Retourne les réservations du prestataire, triées par statut métier.
     *
     * @param utilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @return la liste des résumés de réservations pro
     */
    public List<ProReservationSummaryDto> getProReservations(UUID utilisateurId) {
        return reservationRepository.findByPrestataireUtilisateurIdOrderByStatus(utilisateurId).stream()
                .map(reservationMapper::toProReservationSummaryDto)
                .toList();
    }

    /**
     * Retourne les compteurs du tableau de bord pro (statuts actifs uniquement).
     *
     * @param utilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @return les compteurs par statut actif
     */
    public ProBoardCountsDto getProBoardCounts(UUID utilisateurId) {
        return new ProBoardCountsDto(
                reservationRepository
                        .countByStatusAndPrestataireUtilisateurId(ReservationStatus.NEW, utilisateurId),
                reservationRepository
                        .countByStatusAndPrestataireUtilisateurId(ReservationStatus.IN_DISCUSSION, utilisateurId),
                reservationRepository
                        .countByStatusAndPrestataireUtilisateurId(ReservationStatus.CONFIRMED, utilisateurId)
        );
    }

    /**
     * Vérifie que l'utilisateur est le prestataire lié à la réservation.
     * Appelée par le controller avant toute opération métier sur une réservation pro.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @throws ReservationNotFoundException   si la réservation n'existe pas
     * @throws ReservationNotAllowedException si le prestataire n'est pas lié à cette réservation
     */
    public void verifyProOwnershipByReservationId(UUID reservationId, UUID utilisateurId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        if (!reservation.getPrestataire().getUtilisateur().getId().equals(utilisateurId)) {
            throw new ReservationNotAllowedException();
        }
    }

    /**
     * Retourne le détail d'une réservation pour le prestataire.
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @return le détail de la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     */
    public ProReservationDetailDto getProReservationDetail(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        return reservationMapper.toProReservationDetailDto(reservation);
    }

    /**
     * Marque la réservation comme contactée — passe de NEW à IN_DISCUSSION.
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     * @throws InvalidStateException        si le statut courant n'est pas NEW
     */
    public void markContacted(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        if (reservation.getStatus() != ReservationStatus.NEW) {
            throw new InvalidStateException("La réservation ne peut pas être acceptée depuis le statut " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.IN_DISCUSSION);

        // save reservation
        reservationRepository.save(reservation);

        // crée une note système pour alimenter le feed de la réservation, visible par tout le monde
        noteRepository.save(Note.builder()
                .reservation(reservation)
                .prestataire(reservation.getPrestataire())
                .generatedKey("feed.system.contacted")
                .isPersonal(false)
                .build());
    }

    /**
     * Passe la réservation de IN_DISCUSSION à CONFIRMED.
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     * @throws InvalidStateException        si le statut courant n'est pas IN_DISCUSSION
     */
    public void confirm(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        if (reservation.getStatus() != ReservationStatus.IN_DISCUSSION) {
            throw new InvalidStateException("La réservation ne peut pas être confirmée depuis le statut " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);

        // save reservation
        reservationRepository.save(reservation);

        // crée une note système pour alimenter le feed de la réservation, visible par tout le monde
        noteRepository.save(Note.builder()
                .reservation(reservation)
                .prestataire(reservation.getPrestataire())
                .generatedKey("feed.system.confirmed")
                .isPersonal(false)
                .build());
    }

    /**
     * Annule une réservation confirmée par le prestataire — passe de CONFIRMED à CANCELED_POST_CONFIRMATION.
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     * @throws InvalidStateException        si le statut courant n'est pas CONFIRMED
     */
    public void cancelByPro(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidStateException(
                    "La réservation ne peut pas être annulée par le prestataire depuis le statut " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CANCELED_POST_CONFIRMATION);
        reservationRepository.save(reservation);

        noteRepository.save(Note.builder()
                .reservation(reservation)
                .prestataire(reservation.getPrestataire())
                .generatedKey("feed.system.cancelled")
                .isPersonal(false)
                .build());
    }

    /**
     * Refuse la réservation (NEW → REFUSED_PRE_CONTACT, IN_DISCUSSION → REFUSED_POST_CONTACT).
     * une note visible par le client est créée avec la raison si le prestataire l'a renseignée
     * L'ownership doit être vérifié par l'appelant avant d'invoquer cette méthode.
     *
     * @param reservationId l'identifiant de la réservation
     * @param request       la raison et le choix de communication
     * @throws ReservationNotFoundException si la réservation n'existe pas
     * @throws InvalidStateException        si le statut ne permet pas le refus
     */
    public void refuse(UUID reservationId, RefuseReservationRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        ReservationStatus refused = switch (reservation.getStatus()) {
            case NEW           -> ReservationStatus.REFUSED_PRE_CONTACT;
            case IN_DISCUSSION -> ReservationStatus.REFUSED_POST_CONTACT;
            default -> throw new InvalidStateException(
                    "La réservation ne peut pas être refusée depuis le statut " + reservation.getStatus());
        };
        reservation.setStatus(refused);
        reservationRepository.save(reservation);

        // note générée pour statuer le refus
        noteRepository.save(Note.builder()
                .reservation(reservation)
                .prestataire(reservation.getPrestataire())
                .generatedKey("feed.system.refused")
                .content(request.reason())
                .isPersonal(false)
                .build());
    }

    /**
     * Vérifie que l'utilisateur est le propriétaire de l'événement parent de la réservation.
     * Remonte la chaîne reservation → evenement → utilisateur pour s'assurer de l'ownership complet.
     * Appelée par le controller avant toute opération métier sur une réservation cliente.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @throws ReservationNotFoundException   si la réservation n'existe pas
     * @throws ReservationNotAllowedException si l'utilisateur n'est pas le propriétaire de l'événement parent
     */
    public void verifyOwnershipByReservationId(UUID reservationId, UUID utilisateurId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        if (!reservation.getEvenement().getUtilisateur().getId().equals(utilisateurId)) {
            throw new ReservationNotAllowedException();
        }
    }

    /**
     * Retourne la réservation sans vérification d'ownership.
     * Réservé à un usage interne entre services du même package — l'appelant est responsable
     * de s'assurer que l'ownership a déjà été vérifié au niveau du controller.
     *
     * @param reservationId l'identifiant de la réservation
     * @return la réservation
     * @throws ReservationNotFoundException si la réservation n'existe pas
     */
    Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
    }

    /**
     * Retourne le nombre de réservations d'un statut donné dans une liste de réservations.
     *
     * @param reservations la liste de réservations
     * @param status       le statut recherché
     * @return le compte
     */
    private int count(List<Reservation> reservations, ReservationStatus status) {
        return (int) reservations.stream().filter(r -> status.equals(r.getStatus())).count();
    }

}
