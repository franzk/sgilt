package net.franzka.sgilt.core.evenement.repository;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvenementRepository extends JpaRepository<Evenement, UUID> {}
