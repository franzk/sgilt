package net.franzka.sgilt.core.jwt.repository;

import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link ActionToken}.
 */
@Repository
public interface ActionTokenRepository extends JpaRepository<ActionToken, UUID> {

    /**
     * Recherche un token d'action par son payload HMAC.
     *
     * @param hmacPayload le payload extrait du token reçu par email
     * @return le token correspondant, ou {@link Optional#empty()} si absent
     */
    Optional<ActionToken> findByHmacPayload(String hmacPayload);

    /**
     * Retourne tous les tokens d'action d'un type donné, quel que soit leur état d'expiration.
     *
     * @param type le type d'action recherché
     * @return les tokens correspondants
     */
    List<ActionToken> findByType(ActionType type);
}
