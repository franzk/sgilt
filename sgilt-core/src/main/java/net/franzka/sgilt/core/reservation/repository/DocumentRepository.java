package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA des documents du feed d'une réservation, pour ce qui ne concerne que les documents
 * (l'envoi, {@code uploadId}, n'existe que sur {@link Document}). Le reste du feed passe par
 * {@link ReservationFeedRepository}.
 */
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    /**
     * Retourne tous les documents d'un envoi, supprimés compris.
     *
     * @param uploadId l'identifiant de l'envoi
     * @return les documents de l'envoi
     */
    List<Document> findByUploadId(UUID uploadId);
}
