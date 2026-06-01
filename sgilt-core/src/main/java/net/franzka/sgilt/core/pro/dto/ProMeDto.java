package net.franzka.sgilt.core.pro.dto;

import java.util.UUID;

/**
 * Profil minimal retourné au prestataire connecté.
 *
 * @param id        identifiant de l'utilisateur
 * @param email     adresse email
 * @param firstName prénom
 * @param lastName  nom de famille
 */
public record ProMeDto(UUID id, String email, String firstName, String lastName) {}
