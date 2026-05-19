package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.ReservationFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité de base {@link ReservationFeed} (notes et documents).
 */
public interface ReservationFeedRepository extends JpaRepository<ReservationFeed, UUID> {

    /**
     * Retourne tous les éléments du feed visibles pour une réservation selon le rôle de l'appelant.
     * Les éléments personnels de l'autre partie sont exclus en base — ils ne doivent jamais transiter.
     * CLIENT      : cache les éléments personnels du prestataire.
     * PRESTATAIRE : cache les éléments personnels du client.
     *
     * @param reservationId l'identifiant de la réservation
     * @param caller        rôle de l'appelant
     * @return les éléments visibles triés par date de création
     */
    default List<ReservationFeed> findVisible(UUID reservationId, FeedCaller caller) {
        return findVisibleByCaller(reservationId, caller.name());
    }

    @Query("""
            SELECT f FROM ReservationFeed f
            WHERE f.reservation.id = :reservationId
            AND f.deletedAt IS NULL
            AND NOT (f.isPersonal = true AND (
                (:caller = 'CLIENT'      AND f.prestataire IS NOT NULL) OR
                (:caller = 'PRESTATAIRE' AND f.utilisateur  IS NOT NULL)
            ))
            ORDER BY f.createdAt ASC
            """)
    List<ReservationFeed> findVisibleByCaller(@Param("reservationId") UUID reservationId,
                                              @Param("caller") String caller);
}
