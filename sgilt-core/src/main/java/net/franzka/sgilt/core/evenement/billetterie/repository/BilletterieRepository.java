package net.franzka.sgilt.core.evenement.billetterie.repository;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

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

    /**
     * Retourne la billetterie d'un événement en la verrouillant en écriture jusqu'à la fin de la
     * transaction : les commandes d'une même billetterie se créent l'une après l'autre.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, ou vide si l'événement n'en a pas
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Billetterie> findForUpdateByEvenementId(UUID evenementId);
}
