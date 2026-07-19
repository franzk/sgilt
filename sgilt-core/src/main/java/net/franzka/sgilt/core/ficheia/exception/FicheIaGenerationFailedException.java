package net.franzka.sgilt.core.ficheia.exception;

/**
 * Levée lorsque la génération IA n'a pas produit de résultat exploitable (site inaccessible,
 * contenu insuffisant, réponse structurée vide ou invalide) — consomme un essai comme une
 * génération normale.
 */
public class FicheIaGenerationFailedException extends RuntimeException {

    public FicheIaGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
