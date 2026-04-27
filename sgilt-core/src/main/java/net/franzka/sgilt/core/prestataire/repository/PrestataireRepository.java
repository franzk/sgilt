package net.franzka.sgilt.core.prestataire.repository;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Prestataire}.
 */
@Repository
public interface PrestataireRepository extends JpaRepository<Prestataire, UUID> {

    /**
     * Recherche un prestataire actif par son slug unique.
     *
     * @param slug le slug du prestataire
     * @return le prestataire correspondant, ou vide
     */
    Optional<Prestataire> findBySlugAndDeletedAtIsNull(String slug);

    /**
     * Retourne tous les prestataires actifs.
     *
     * @return liste des prestataires non supprimés
     */
    List<Prestataire> findByDeletedAtIsNull();

    /**
     * Retourne les prestataires actifs d'une catégorie.
     *
     * @param categoryKey clé de catégorie ('musique', 'restauration'…)
     * @return prestataires correspondants
     */
    List<Prestataire> findByCategoryKeyAndDeletedAtIsNull(String categoryKey);

    /**
     * Retourne les prestataires actifs ayant au moins une des sous-catégories données.
     *
     * @param subcatKeys clés de sous-catégories ('dj', 'pop-rock'…)
     * @return prestataires correspondants
     */
    List<Prestataire> findBySubcatKeysInAndDeletedAtIsNull(Collection<String> subcatKeys);
}
