package net.franzka.sgilt.core.ficheia.exception;

/**
 * Levée lorsque le quota de générations IA restantes du prestataire est épuisé.
 */
public class FicheIaQuotaExhaustedException extends RuntimeException {

    public FicheIaQuotaExhaustedException(String prestataireId) {
        super("Quota de générations IA épuisé pour le prestataire " + prestataireId);
    }
}
