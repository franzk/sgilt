package net.franzka.sgilt.core.prestataire.repository;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'entité {@link Categorie}.
 */
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, String> {

    /**
     * Retourne toutes les catégories triées, avec leurs sous-catégories chargées dans la même
     * requête.
     *
     * @param sort le tri à appliquer aux catégories
     * @return les catégories et leurs sous-catégories
     */
    @Override
    @EntityGraph(attributePaths = "subcategories")
    List<Categorie> findAll(Sort sort);
}
