package net.franzka.sgilt.core.ficheia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mapping HTTP des exceptions du domaine génération IA de fiche.
 */
@RestControllerAdvice
public class FicheIaExceptionHandler {

    /**
     * Quota épuisé : le frontend doit afficher un état "plus d'essais" dédié.
     */
    @ExceptionHandler(FicheIaQuotaExhaustedException.class)
    public ResponseEntity<Void> handleQuotaExhausted(FicheIaQuotaExhaustedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Échec technique de l'appel OpenAI (réseau, timeout, erreur serveur) : réessayable, aucun
     * essai consommé — le frontend doit afficher un état "réessayer".
     */
    @ExceptionHandler(FicheIaTechnicalException.class)
    public ResponseEntity<Void> handleTechnicalError(FicheIaTechnicalException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * Échec lié aux données fournies (site inaccessible, contenu insuffisant, réponse IA vide) :
     * un essai est tout de même consommé.
     */
    @ExceptionHandler(FicheIaGenerationFailedException.class)
    public ResponseEntity<Void> handleGenerationFailed(FicheIaGenerationFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).build();
    }

    /**
     * Combinaison section/action invalide dans la commande d'application (ex. AJOUTER sur une
     * section à valeur simple).
     */
    @ExceptionHandler(FicheIaInvalidInstructionException.class)
    public ResponseEntity<Void> handleInvalidInstruction(FicheIaInvalidInstructionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Aucune génération IA exploitable disponible pour appliquer la commande.
     */
    @ExceptionHandler(FicheIaNoResultAvailableException.class)
    public ResponseEntity<Void> handleNoResultAvailable(FicheIaNoResultAvailableException ex) {
        return ResponseEntity.notFound().build();
    }
}
