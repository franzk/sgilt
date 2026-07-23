package net.franzka.sgilt.core.ficheia.exception;

/**
 * Levée lorsque la combinaison section/action de la commande d'application est invalide
 * (ex. AJOUTER sur une section à valeur simple, section manquante ou en trop selon l'action).
 */
public class FicheIaInvalidInstructionException extends RuntimeException {

    public FicheIaInvalidInstructionException(String message) {
        super(message);
    }
}
