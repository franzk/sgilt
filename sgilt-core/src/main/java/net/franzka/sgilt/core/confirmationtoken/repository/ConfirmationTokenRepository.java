package net.franzka.sgilt.core.confirmationtoken.repository;

import net.franzka.sgilt.core.confirmationtoken.domain.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {
    Optional<ConfirmationToken> findByJti(String jti);
}
