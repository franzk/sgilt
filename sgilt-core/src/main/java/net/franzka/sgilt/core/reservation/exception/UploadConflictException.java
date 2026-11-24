package net.franzka.sgilt.core.reservation.exception;

import java.util.UUID;

/**
 * Exception levée quand un document est déposé dans un envoi ({@code uploadId}) déjà utilisé sur une
 * autre réservation, par l'autre partie, ou avec une autre visibilité : un envoi n'a qu'un auteur,
 * une réservation et une visibilité, celles de sa carte dans le feed.
 */
public class UploadConflictException extends RuntimeException {

    public UploadConflictException(UUID uploadId) {
        super("Envoi " + uploadId + " déjà utilisé sur une autre réservation, par l'autre partie ou avec une autre visibilité");
    }
}
