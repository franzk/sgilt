package net.franzka.sgilt.core.utilisateur.service;

import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurAlreadyExistException;
import net.franzka.sgilt.core.utilisateur.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier pour l'entité {@link Utilisateur}.
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param utilisateurRepository le repository des utilisateurs
     */
    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Crée et persiste un nouvel utilisateur en statut ACTIVE.
     * Lève une exception si un utilisateur avec le même email existe déjà.
     *
     * @param firstName le prénom de l'utilisateur
     * @param lastName  le nom de l'utilisateur
     * @param email     l'adresse email de l'utilisateur
     * @param phone     le numéro de téléphone de l'utilisateur (nullable)
     * @return l'entité {@link Utilisateur} créée et persistée
     * @throws UtilisateurAlreadyExistException si un utilisateur avec cet email existe déjà
     */
    public Utilisateur createUtilisateur(String firstName, String lastName, String email, String phone) {
        if (utilisateurRepository.existsByEmail(email)) {
            throw new UtilisateurAlreadyExistException();
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .status(UtilisateurStatus.ACTIVE)
                .build();

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Vérifie si un utilisateur est déjà enregistré pour l'email donné.
     *
     * @param email l'adresse email à vérifier
     * @return {@code true} si un utilisateur existe pour cet email, {@code false} sinon
     */
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}
