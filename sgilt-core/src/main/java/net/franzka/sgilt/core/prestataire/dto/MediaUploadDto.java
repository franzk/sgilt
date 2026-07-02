package net.franzka.sgilt.core.prestataire.dto;

/**
 * Réponse à l'upload d'un média prestataire vers R2.
 *
 * @param key chemin R2 du fichier uploadé (ex. {@code uploads/uuid.jpg})
 */
public record MediaUploadDto(String key) {}
