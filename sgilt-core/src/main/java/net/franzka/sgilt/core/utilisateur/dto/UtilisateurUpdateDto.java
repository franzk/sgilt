package net.franzka.sgilt.core.utilisateur.dto;

/**
 * DTO de mise à jour du profil de l'utilisateur connecté (PATCH).
 * Sémantique : null = ne pas toucher le champ, valeur/"" = écrire tel quel.
 */
public record UtilisateurUpdateDto(
        String firstName,
        String lastName,
        String phone
) {}
