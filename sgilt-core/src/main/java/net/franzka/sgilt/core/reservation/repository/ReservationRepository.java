package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
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

    /**
     * Retourne les réservations d'un utilisateur dont le statut fait partie de la liste fournie.
     *
     * @param utilisateurId l'identifiant de l'utilisateur
     * @param statuses      les statuts à inclure
     * @return la liste des réservations correspondantes
     */
    List<Reservation> findByUtilisateurIdAndStatusIn(UUID utilisateurId, List<ReservationStatus> statuses);

    /**
     * Vérifie si l'utilisateur possède au moins une réservation dans le statut donné.
     *
     * @param utilisateurId l'identifiant de l'utilisateur
     * @param status        le statut recherché
     * @return true si une réservation existe
     */
    boolean existsByUtilisateurIdAndStatus(UUID utilisateurId, ReservationStatus status);

    /**
     * Retourne toutes les réservations dont le prestataire est lié à l'utilisateur donné.
     *
     * @param utilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @return la liste des réservations
     */
    List<Reservation> findByPrestataireUtilisateurIdOrderByStatus(UUID utilisateurId);

    /**
     * Retourne le compte de réservations par statut pour un prestataire.
     * @param status le statut des réservations à compter
     * @param prestataireUtilisateurId l'identifiant de l'utilisateur lié au prestataire
     * @return le compte de réservations correspondant
     */
    int countByStatusAndPrestataireUtilisateurId(ReservationStatus status, UUID prestataireUtilisateurId);

    /**
     * Retourne toutes les réservations d'un prestataire.
     *
     * @param prestataireId l'identifiant du prestataire
     * @return la liste des réservations
     */
    List<Reservation> findByPrestataireId(UUID prestataireId);

    /**
     * Retourne toutes les réservations, triées par date de création décroissante.
     * Utilisé par le back-office admin.
     *
     * @return la liste des réservations, la plus récente en premier
     */
    List<Reservation> findAllByOrderByCreatedAtDesc();

    /**
     * Retourne les réservations d'un statut donné, triées par date de création décroissante.
     * Utilisé par le back-office admin.
     *
     * @param status le statut à filtrer
     * @return la liste des réservations correspondantes, la plus récente en premier
     */
    List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);

}
