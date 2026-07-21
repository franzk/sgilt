package net.franzka.sgilt.core.utilisateur.service;

import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurAlreadyExistException;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurNotFoundException;
import net.franzka.sgilt.core.utilisateur.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service métier pour l'entité {@link Utilisateur}.
 */
@Service
public class UtilisateurService {

    /**
     * Date de version des CGU et de la politique de confidentialité en vigueur.
     * Correspond à la date affichée en tête des documents {@code /m/cgu} et {@code /m/confidentialite}.
     * Le texte exact accepté pour cette version est retrouvable dans l'historique Git.
     */
    private static final String CGU_VERSION = "2026-06";

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
     * Enregistre l'acceptation des CGU et de la politique de confidentialité par l'utilisateur,
     * pour la version actuellement en vigueur, avec l'horodatage du moment de l'acceptation.
     *
     * @param utilisateur l'utilisateur qui accepte les CGU et la politique de confidentialité
     */
    public void acceptCgu(Utilisateur utilisateur) {
        utilisateur.setCguVersionAccepted(CGU_VERSION);
        utilisateur.setCguAcceptedAt(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Résout l'utilisateur correspondant à l'email de l'utilisateur authentifié.
     * Utilisé par les services qui ont besoin de l'entité (ex. pour récupérer son id).
     *
     * @param email l'email extrait du JWT
     * @return l'entité {@link Utilisateur}
     * @throws UtilisateurNotFoundException si aucun utilisateur ne correspond
     */
    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmailIgnoreCase(email)
                .orElseThrow(UtilisateurNotFoundException::new);
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

    /**
     * Retourne le profil de l'utilisateur identifié par son email.
     *
     * @param email l'adresse email de l'utilisateur connecté
     * @return le profil de l'utilisateur
     * @throws UtilisateurNotFoundException si aucun utilisateur ne correspond à cet email
     */
    public UtilisateurProfileDto getProfile(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(email)
                .orElseThrow(UtilisateurNotFoundException::new);
        return new UtilisateurProfileDto(
                utilisateur.getFirstName(),
                utilisateur.getLastName(),
                utilisateur.getEmail(),
                utilisateur.getAvatarUrl()
        );
    }
}
