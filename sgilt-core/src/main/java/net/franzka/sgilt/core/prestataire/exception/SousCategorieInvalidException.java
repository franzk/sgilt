package net.franzka.sgilt.core.prestataire.exception;

import lombok.Getter;
import net.franzka.sgilt.core.prestataire.domain.SousCategorieError;

/**
 * Exception levée lorsqu'une modification des sous-catégories enfreint une règle métier.
 */
@Getter
public class SousCategorieInvalidException extends RuntimeException {

    private final SousCategorieError reason;

    /**
     * Construit l'exception pour un motif de refus.
     *
     * @param reason  le motif de refus
     * @param message le détail du refus
     */
    public SousCategorieInvalidException(SousCategorieError reason, String message) {
        super(message);
        this.reason = reason;
    }
}
