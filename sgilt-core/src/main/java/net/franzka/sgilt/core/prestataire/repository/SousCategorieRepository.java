package net.franzka.sgilt.core.prestataire.repository;

import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'entité {@link SousCategorie}.
 */
@Repository
public interface SousCategorieRepository extends JpaRepository<SousCategorie, String> {

    /**
     * Retourne les sous-catégories d'une catégorie, par rang d'affichage.
     *
     * @param categoryKey la clé de la catégorie
     * @return les sous-catégories de la catégorie
     */
    List<SousCategorie> findByCategoryKeyOrderByPositionAsc(String categoryKey);
}
