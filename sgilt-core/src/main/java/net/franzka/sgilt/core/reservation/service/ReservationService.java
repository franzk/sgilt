package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.mapper.ReservationMapper;
import net.franzka.sgilt.core.reservation.repository.NoteRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .hidden(false)
                .content(initialMessage)
                .build();
        noteRepository.save(note);

        return reservation;
    }

    /**
     * Retourne le nombre de réservations d'un statut donné dans une liste de réservations.
     *
     * @param reservations la liste de réservations
     * @param status le status recherché
     * @return le compte
     */
    private int count(List<Reservation> reservations, ReservationStatus status) {
        return (int)reservations.stream().filter(r -> status.equals(r.getStatus())).count();
    }
}
