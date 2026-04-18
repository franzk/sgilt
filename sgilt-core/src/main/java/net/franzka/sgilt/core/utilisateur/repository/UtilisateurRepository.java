package net.franzka.sgilt.core.utilisateur.repository;

import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Utilisateur}.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {

    /**
     * Vérifie si un utilisateur existe pour l'email donné.
     *
     * @param email l'adresse email à vérifier
     * @return {@code true} si un utilisateur avec cet email existe, {@code false} sinon
     */
    boolean existsByEmail(String email);
}
