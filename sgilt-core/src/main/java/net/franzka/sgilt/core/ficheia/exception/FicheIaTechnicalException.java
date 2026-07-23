package net.franzka.sgilt.core.ficheia.exception;

/**
 * Levée en cas d'échec technique de l'appel au service de génération IA (réseau, timeout, erreur
 * serveur OpenAI) — indépendant de l'URL ou du contenu du site fourni. Ne consomme pas d'essai :
 * le prestataire peut réessayer sans pénalité.
 */
public class FicheIaTechnicalException extends RuntimeException {

    public FicheIaTechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
