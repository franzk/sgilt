package net.franzka.sgilt.core.evenement.repository;

import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JournalEvenementRepository extends JpaRepository<JournalEvenement, UUID> {}
