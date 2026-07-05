package net.franzka.sgilt.core.jwt.repository;

import net.franzka.sgilt.core.jwt.domain.ActionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link ActionToken}.
 */
@Repository
public interface ActionTokenRepository extends JpaRepository<ActionToken, UUID> {
}
