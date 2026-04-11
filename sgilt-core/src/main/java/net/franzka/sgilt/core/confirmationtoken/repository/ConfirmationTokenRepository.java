package net.franzka.sgilt.core.confirmationtoken.repository;

import net.franzka.sgilt.core.confirmationtoken.domain.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {

    Optional<ConfirmationToken> findByJti(String jti);

    @Modifying
    @Query("UPDATE ConfirmationToken ct SET ct.used = true WHERE ct.id = :id")
    void markAsUsed(@Param("id") UUID id);
}
