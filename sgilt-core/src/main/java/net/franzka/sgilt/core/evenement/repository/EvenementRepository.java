package net.franzka.sgilt.core.evenement.repository;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Evenement}.
 */
public interface EvenementRepository extends JpaRepository<Evenement, UUID> {

    /**
     * Retourne tous les événements appartenant à l'utilisateur identifié par son id.
     *
     * @param utilisateurId l'identifiant de l'utilisateur
     * @return la liste des événements
     */
    List<Evenement> findByUtilisateurId(UUID utilisateurId);
}
