package net.franzka.sgilt.core.utilisateur.dto;

/**
 * Champs éditables du profil de l'utilisateur connecté (GET /users/me/edit).
 *
 * @param firstName prénom
 * @param lastName  nom de famille
 * @param phone     numéro de téléphone, ou {@code null} si non renseigné
 * @param email     adresse email
 */
public record UtilisateurEditDto(
        String firstName,
        String lastName,
        String phone,
        String email
) {}
