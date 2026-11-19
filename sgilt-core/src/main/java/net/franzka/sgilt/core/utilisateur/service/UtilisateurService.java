package net.franzka.sgilt.core.utilisateur.service;

import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurEditDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurUpdateDto;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurAlreadyExistException;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurNotFoundException;
import net.franzka.sgilt.core.utilisateur.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        return buildAndSave(firstName, lastName, email, phone, UtilisateurStatus.ACTIVE);
    }

    /**
     * Crée et persiste un client invité depuis le BO, en statut INVITED : son compte n'est pas
     * encore activé (il passera ACTIVE à l'activation, via le lien de l'invitation).
     *
     * @param firstName le prénom du client
     * @param lastName  le nom du client
     * @param email     l'adresse email du client
     * @return l'entité {@link Utilisateur} créée et persistée
     * @throws UtilisateurAlreadyExistException si un utilisateur avec cet email existe déjà
     */
    public Utilisateur createInvitedUtilisateur(String firstName, String lastName, String email) {
        return buildAndSave(firstName, lastName, email, null, UtilisateurStatus.INVITED);
    }

    /**
     * Liste les clients invités depuis le BO qui n'ont pas encore activé leur compte.
     *
     * @return les utilisateurs en statut INVITED
     */
    public List<Utilisateur> listInvitedUtilisateurs() {
        return utilisateurRepository.findByStatus(UtilisateurStatus.INVITED);
    }

    /**
     * Retourne le client invité depuis le BO qui n'a pas encore activé son compte.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur, en statut INVITED
     * @throws UtilisateurNotFoundException si aucun utilisateur ne correspond, ou s'il n'est plus
     *                                      INVITED (compte déjà activé)
     */
    public Utilisateur getInvitedUtilisateur(UUID id) {
        return utilisateurRepository.findById(id)
                .filter(utilisateur -> utilisateur.getStatus() == UtilisateurStatus.INVITED)
                .orElseThrow(UtilisateurNotFoundException::new);
    }

    /**
     * Active le compte d'un client invité depuis le BO : passe de INVITED à ACTIVE.
     *
     * @param email l'email du client invité
     * @return l'utilisateur activé
     * @throws UtilisateurNotFoundException si aucun utilisateur ne correspond, ou s'il n'est pas
     *                                      INVITED (compte déjà activé)
     */
    public Utilisateur activateInvitedUtilisateur(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(email)
                .filter(found -> found.getStatus() == UtilisateurStatus.INVITED)
                .orElseThrow(UtilisateurNotFoundException::new);
        return activate(utilisateur);
    }

    /**
     * Active à sa connexion un client invité depuis le BO qui n'est pas passé par le lien de son
     * invitation (lien expiré, puis « mot de passe oublié » sur Keycloak) : passe de INVITED à ACTIVE,
     * pour que ses notifications ne restent pas coupées. Ses CGU ne sont pas acceptées — parcours
     * marginal, assumé. Sans effet pour tout autre utilisateur.
     *
     * @param email l'email de l'utilisateur qui se connecte
     */
    public void activateInvitedOnLogin(String email) {
        utilisateurRepository.findByEmailIgnoreCase(email)
                .filter(found -> found.getStatus() == UtilisateurStatus.INVITED)
                .ifPresent(this::activate);
    }

    // Passe un client invité de INVITED à ACTIVE.
    private Utilisateur activate(Utilisateur utilisateur) {
        utilisateur.setStatus(UtilisateurStatus.ACTIVE);
        return utilisateurRepository.save(utilisateur);
    }

    // Crée l'utilisateur dans le statut donné, après avoir vérifié que son email est libre.
    private Utilisateur buildAndSave(String firstName, String lastName, String email, String phone,
                                     UtilisateurStatus status) {
        if (utilisateurRepository.existsByEmail(email)) {
            throw new UtilisateurAlreadyExistException();
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .status(status)
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
     * Vérifie si un utilisateur est déjà enregistré pour l'email donné, quelle que soit sa casse
     * (Keycloak refuse un email qui ne diffère que par la casse). Utilisé par l'invitation client du
     * BO ; les autres parcours passent encore par {@link #existsByEmail} (chantier global de la casse
     * des emails, au backlog).
     *
     * @param email l'adresse email à vérifier
     * @return {@code true} si un utilisateur existe pour cet email, toutes casses confondues
     */
    public boolean existsByEmailIgnoreCase(String email) {
        return utilisateurRepository.existsByEmailIgnoreCase(email);
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

    /**
     * Retourne les champs éditables du profil de l'utilisateur (prénom, nom, téléphone, email).
     *
     * @param utilisateur l'utilisateur connecté
     * @return le profil éditable
     */
    public UtilisateurEditDto getEditProfile(Utilisateur utilisateur) {
        return new UtilisateurEditDto(
                utilisateur.getFirstName(),
                utilisateur.getLastName(),
                utilisateur.getPhone(),
                utilisateur.getEmail()
        );
    }

    /**
     * Met à jour le prénom et/ou le nom de l'utilisateur. Seuls les champs non-null du DTO sont écrits.
     *
     * @param utilisateur l'utilisateur connecté
     * @param dto         les champs à mettre à jour (null = non modifié)
     */
    public void updateProfile(Utilisateur utilisateur, UtilisateurUpdateDto dto) {
        if (dto.firstName() != null) {
            utilisateur.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            utilisateur.setLastName(dto.lastName());
        }
        if (dto.phone() != null) {
            utilisateur.setPhone(dto.phone());
        }
        utilisateurRepository.save(utilisateur);
    }
}
