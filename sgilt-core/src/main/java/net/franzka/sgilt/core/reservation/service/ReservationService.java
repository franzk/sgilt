package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationItemDto;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.exception.InvalidStateException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotAllowedException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotFoundException;
import net.franzka.sgilt.core.reservation.mapper.ReservationMapper;
import net.franzka.sgilt.core.reservation.repository.NoteRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
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
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return les métadonnées de la réservation
     * @throws ReservationNotFoundException   si la réservation n'existe pas
     * @throws ReservationNotAllowedException si l'utilisateur n'est pas le propriétaire
     */
    public ReservationMetaDto getMeta(UUID reservationId, UUID utilisateurId) {
        Reservation reservation = getReservation(reservationId, utilisateurId);
        Prestataire p = reservation.getPrestataire();
        return new ReservationMetaDto(
                reservation.getId(),
                p.getId(),
                p.getName(),
                p.getAvatar() != null ? p.getAvatar() : p.getHeroImage(),
                p.getCategoryKey(),
                reservationMapper.mapStatus(reservation.getStatus()),
                0
        );
    }

    /**
     * Annule une réservation. Transitions valides : NEW → CANCELED_BY_CLIENT_PRE_CONTACT,
     * IN_DISCUSSION → CANCELED_BY_CLIENT_POST_CONTACT.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @throws ReservationNotFoundException   si la réservation n'existe pas
     * @throws ReservationNotAllowedException si l'utilisateur n'est pas le propriétaire
     * @throws InvalidStateException          si le statut ne permet pas l'annulation
     */
    public void cancel(UUID reservationId, UUID utilisateurId) {
        Reservation reservation = getReservation(reservationId, utilisateurId);
        ReservationStatus next = switch (reservation.getStatus()) {
            case NEW           -> ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT;
            case IN_DISCUSSION -> ReservationStatus.CANCELED_BY_CLIENT_POST_CONTACT;
            default -> throw new InvalidStateException(
                    "La réservation ne peut pas être annulée depuis le statut " + reservation.getStatus());
        };
        reservation.setStatus(next);
        reservationRepository.save(reservation);
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
     * Retourne la réservation identifiée par son id après vérification de l'ownership.
     * Utilisé en interne et par {@link ReservationFeedService}.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return la réservation
     * @throws ReservationNotFoundException   si la réservation n'existe pas
     * @throws ReservationNotAllowedException si l'utilisateur n'est pas le propriétaire
     */
    Reservation getReservation(UUID reservationId, UUID utilisateurId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        if (!reservation.getUtilisateur().getId().equals(utilisateurId)) {
            throw new ReservationNotAllowedException();
        }
        return reservation;
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
