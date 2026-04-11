package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Reservation}.
 */
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {}
