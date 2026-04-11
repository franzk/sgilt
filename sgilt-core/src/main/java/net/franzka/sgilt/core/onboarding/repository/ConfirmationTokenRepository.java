package net.franzka.sgilt.core.onboarding.repository;

import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link ConfirmationToken}.
 */
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {

    /**
     * Recherche un token de confirmation par son identifiant JWT (jti).
     *
     * @param jti l'identifiant unique du JWT
     * @return le token correspondant, ou {@link Optional#empty()} si absent
     */
    Optional<ConfirmationToken> findByJti(String jti);
}
