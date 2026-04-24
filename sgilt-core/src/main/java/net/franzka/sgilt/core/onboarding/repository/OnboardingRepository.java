package net.franzka.sgilt.core.onboarding.repository;

import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Onboarding}.
 */
@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, UUID> {

    /**
     * Recherche une session d'onboarding par son payload HMAC.
     *
     * @param hmacPayload le payload extrait du token envoyé par email
     * @return la session correspondante, ou {@link Optional#empty()} si absente
     */
    Optional<Onboarding> findByHmacPayload(String hmacPayload);

    /**
     * Recherche les sessions d'onboarding par email et état.
     *
     * @param email l'adresse email associée
     * @param state l'état recherché
     * @return la liste des sessions correspondantes
     */
    List<Onboarding> findByEmailAndState(String email, OnboardingState state);
}
