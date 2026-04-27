package net.franzka.sgilt.core.reservation.repository;

import net.franzka.sgilt.core.reservation.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Note}.
 */
public interface NoteRepository extends JpaRepository<Note, UUID> {}
