package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Reservation}.
 */
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * Retourne toutes les réservations associées à un événement.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la liste des réservations
     */
    List<Reservation> findByEvenementId(UUID evenementId);

    /**
     * Vérifie qu'au moins une réservation lie le prestataire (identifié par son utilisateur) à l'événement.
     *
     * @param evenementId   l'identifiant de l'événement
     * @param utilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @return true si une réservation existe
     */
    boolean existsByEvenementIdAndPrestataireUtilisateurId(UUID evenementId, UUID utilisateurId);
}
