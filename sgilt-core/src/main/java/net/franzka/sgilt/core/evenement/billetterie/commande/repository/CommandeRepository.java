package net.franzka.sgilt.core.evenement.billetterie.commande.repository;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Commande}.
 */
public interface CommandeRepository extends JpaRepository<Commande, UUID> {

    /**
     * Retourne les commandes d'une billetterie dans un statut donné.
     *
     * @param billetterieId l'identifiant de la billetterie
     * @param status        le statut
     * @return les commandes
     */
    List<Commande> findByBilletterieIdAndStatus(UUID billetterieId, CommandeStatus status);

    /**
     * Retourne les commandes d'une billetterie dans un statut donné, qui expirent après un instant.
     *
     * @param billetterieId l'identifiant de la billetterie
     * @param status        le statut
     * @param instant       l'instant
     * @return les commandes
     */
    List<Commande> findByBilletterieIdAndStatusAndExpiresAtAfter(UUID billetterieId, CommandeStatus status,
                                                                 LocalDateTime instant);
}
