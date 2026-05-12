package net.franzka.sgilt.core.evenement.repository;

import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalEvenementRepository extends JpaRepository<JournalEvenement, UUID> {

    /**
     * Retourne l'entrée de journal la plus récente pour un événement.
     *
     * @param evenementId l'identifiant de l'événement
     * @return l'entrée la plus récente, ou vide si aucune
     */
    Optional<JournalEvenement> findFirstByEvenementIdOrderByCreatedAtDesc(UUID evenementId);

    /**
     * Retourne une page d'entrées de journal pour un événement, de la plus récente à la plus ancienne.
     *
     * @param evenementId l'identifiant de l'événement
     * @param pageable    les paramètres de pagination
     * @return une page d'entrées de journal
     */
    Page<JournalEvenement> findByEvenementIdOrderByCreatedAtDesc(UUID evenementId, Pageable pageable);
}
