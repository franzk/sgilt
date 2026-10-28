package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.ReservationReminder;
import net.franzka.sgilt.core.reservation.domain.ReservationReminderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link ReservationReminder}.
 */
public interface ReservationReminderRepository extends JpaRepository<ReservationReminder, UUID> {

    /**
     * Vérifie si un rappel du type donné a déjà été envoyé pour la réservation.
     *
     * @param reservationId l'identifiant de la réservation
     * @param type          le type de rappel
     * @return true si le rappel a déjà été envoyé
     */
    boolean existsByReservationIdAndType(UUID reservationId, ReservationReminderType type);
}
