package net.franzka.sgilt.core.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.exception.InvalidStateException;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Reservation}.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * Crée et persiste une réservation en statut DRAFT pour un événement et un prestataire donnés.
     *
     * @param evenement     l'événement associé
     * @param prestataireId l'identifiant du prestataire ciblé
     * @return la réservation sauvegardée
     */
    public Reservation createDraft(Evenement evenement, UUID prestataireId) {
        Reservation reservation = Reservation.builder()
                .evenement(evenement)
                .prestataireId(prestataireId)
                .status(ReservationStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();
        return reservationRepository.save(reservation);
    }

    /**
     * Charge une réservation par son identifiant, passe son statut à NOUVELLE et sauvegarde la modification.
     *
     * @param reservationId l'identifiant de la réservation à activer
     * @throws EntityNotFoundException si aucune réservation ne correspond à cet identifiant
     * @throws InvalidStateException   si la réservation n'est pas en statut DRAFT
     */
    public void activateReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(EntityNotFoundException::new);
        if (reservation.getStatus() != ReservationStatus.DRAFT) {
            throw new InvalidStateException(
                    "La réservation %s n'est pas en statut DRAFT (statut actuel : %s)"
                            .formatted(reservation.getId(), reservation.getStatus())
            );
        }
        reservation.setStatus(ReservationStatus.NOUVELLE);
        reservationRepository.save(reservation);
    }
}
