package net.franzka.sgilt.core.evenement.billetterie.commande.exception;

/**
 * Levée quand le PDF d'un e-billet ne peut pas être généré (gabarit invalide, erreur du moteur PDF).
 * Erreur technique, pas un cas métier : pas de mapping HTTP dédié (500).
 */
public class EBilletPdfException extends RuntimeException {

    /**
     * Construit l'exception à partir de l'erreur du moteur PDF.
     *
     * @param cause l'erreur d'origine
     */
    public EBilletPdfException(Throwable cause) {
        super("Impossible de générer le PDF de l'e-billet", cause);
    }
}
