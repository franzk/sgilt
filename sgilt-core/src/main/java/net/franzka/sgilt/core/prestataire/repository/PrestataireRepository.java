package net.franzka.sgilt.core.prestataire.repository;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Prestataire}.
 */
@Repository
public interface PrestataireRepository extends JpaRepository<Prestataire, UUID> {}
