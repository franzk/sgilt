package net.franzka.sgilt.core.reservation.dto;

import java.util.UUID;

/**
 * Un fichier d'un envoi de documents du feed d'une réservation (voir {@link FeedItemDto#files()}).
 *
 * @param id       l'identifiant du document — celui qu'on télécharge et qu'on supprime
 * @param name     le nom du fichier
 * @param fileType le type MIME du fichier
 * @param url      l'URL de téléchargement du document
 */
public record FeedFileDto(UUID id, String name, String fileType, String url) {}
