package net.franzka.sgilt.core.ficheia.repository;

import net.franzka.sgilt.core.ficheia.domain.GenerationIa;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link GenerationIa}.
 */
@Repository
public interface GenerationIaRepository extends JpaRepository<GenerationIa, UUID> {

    /**
     * Recherche la ligne de génération IA d'un prestataire donné.
     *
     * @param prestataire le prestataire concerné
     * @return la génération IA correspondante, ou vide si aucune génération n'a encore été lancée
     */
    Optional<GenerationIa> findByPrestataire(Prestataire prestataire);
}
