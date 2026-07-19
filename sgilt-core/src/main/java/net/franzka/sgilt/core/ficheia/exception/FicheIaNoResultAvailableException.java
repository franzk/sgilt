package net.franzka.sgilt.core.ficheia.exception;

/**
 * Levée lorsqu'aucune génération IA exploitable n'est disponible pour le prestataire (aucune
 * génération lancée, ou dernière tentative sans résultat exploitable).
 */
public class FicheIaNoResultAvailableException extends RuntimeException {

    public FicheIaNoResultAvailableException(String prestataireId) {
        super("Aucun résultat de génération IA disponible pour le prestataire " + prestataireId);
    }
}
