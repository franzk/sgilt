package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Note}.
 */
public interface NoteRepository extends JpaRepository<Note, UUID> {

    /**
     * Retourne les notes visibles pour une réservation selon le rôle de l'appelant.
     * Les notes personnelles de l'autre partie sont exclues en base — elles ne doivent jamais transiter.
     * CLIENT      : cache les notes personnelles du prestataire.
     * PRESTATAIRE : cache les notes personnelles du client.
     *
     * @param reservationId l'identifiant de la réservation
     * @param caller        rôle de l'appelant
     * @return les notes visibles triées par date de création
     */
    default List<Note> findVisible(UUID reservationId, FeedCaller caller) {
        return findVisibleByCaller(reservationId, caller.name());
    }

    @Query("""
            SELECT n FROM Note n
            WHERE n.reservation.id = :reservationId
            AND n.deletedAt IS NULL
            AND NOT (n.isPersonal = true AND (
                (:caller = 'CLIENT'      AND n.prestataire IS NOT NULL) OR
                (:caller = 'PRESTATAIRE' AND n.utilisateur  IS NOT NULL)
            ))
            ORDER BY n.createdAt ASC
            """)
    List<Note> findVisibleByCaller(@Param("reservationId") UUID reservationId,
                                   @Param("caller") String caller);
}
