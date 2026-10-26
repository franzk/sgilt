package net.franzka.sgilt.core.evenement.billetterie.repository;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Billetterie}.
 */
public interface BilletterieRepository extends JpaRepository<Billetterie, UUID> {

    /**
     * Retourne la billetterie d'un événement.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, ou vide si l'événement n'en a pas
     */
    Optional<Billetterie> findByEvenementId(UUID evenementId);
}
