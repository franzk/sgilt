package net.franzka.sgilt.core.ficheia.exception;

/**
 * Levée lorsque OpenAI ne retourne aucune sortie structurée exploitable (contenu insuffisant sur
 * le site source, réponse incomplète ou refusée par le modèle). N'a rien à voir avec un problème
 * technique de l'appel — considérée comme un essai de génération consommé.
 */
public class FicheIaEmptyResultException extends RuntimeException {

    public FicheIaEmptyResultException(String message) {
        super(message);
    }
}
