package net.franzka.sgilt.core.prestataire.exception;

/**
 * Levée quand un utilisateur tente de modifier la fiche d'un autre prestataire.
 */
public class PrestataireForbiddenException extends RuntimeException {
    public PrestataireForbiddenException(String id) {
        super("Accès interdit à la fiche prestataire : " + id);
    }
}
