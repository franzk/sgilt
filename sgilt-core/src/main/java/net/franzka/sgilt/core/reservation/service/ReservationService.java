package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service métier pour l'entité {@link Reservation}.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * Crée et persiste une réservation en statut NEW.
     *
     * @param evenement    l'événement associé
     * @param prestataire  le prestataire ciblé
     * @param utilisateur  l'utilisateur à l'origine de la réservation
     * @param date         la date souhaitée pour la prestation
     * @return la réservation sauvegardée
     */
    public Reservation create(Evenement evenement, Prestataire prestataire, Utilisateur utilisateur, LocalDate date) {
        Reservation reservation = Reservation.builder()
                .evenement(evenement)
                .prestataire(prestataire)
                .utilisateur(utilisateur)
                .date(date)
                .status(ReservationStatus.NEW)
                .build();
        return reservationRepository.save(reservation);
    }
}
