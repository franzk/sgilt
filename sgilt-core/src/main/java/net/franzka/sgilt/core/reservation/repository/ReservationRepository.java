package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Modifying
    @Query("UPDATE Reservation r SET r.status = :status WHERE r.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") ReservationStatus status);
}
