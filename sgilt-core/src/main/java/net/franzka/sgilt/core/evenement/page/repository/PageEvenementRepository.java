package net.franzka.sgilt.core.evenement.page.repository;

import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link PageEvenement}.
 */
public interface PageEvenementRepository extends JpaRepository<PageEvenement, UUID> {

    /**
     * Retourne la page d'événement correspondant à son slug.
     *
     * @param slug le slug de la page (/e/{slug})
     * @return la page, ou vide si le slug est inconnu
     */
    Optional<PageEvenement> findBySlug(String slug);

    /**
     * Retourne la page d'un événement.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la page, ou vide si l'événement n'en a pas
     */
    Optional<PageEvenement> findByEvenementId(UUID evenementId);
}
