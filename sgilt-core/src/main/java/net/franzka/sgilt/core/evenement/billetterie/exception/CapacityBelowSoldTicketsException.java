package net.franzka.sgilt.core.evenement.billetterie.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'on baisse la jauge d'une billetterie sous ses places vendues.
 */
public class CapacityBelowSoldTicketsException extends RuntimeException {

    /**
     * Construit l'exception pour la billetterie concernée.
     *
     * @param billetterieId l'identifiant de la billetterie
     * @param capacity      la jauge demandée
     * @param soldTickets   les places vendues
     */
    public CapacityBelowSoldTicketsException(UUID billetterieId, int capacity, int soldTickets) {
        super("Jauge " + capacity + " sous les " + soldTickets + " places vendues de la billetterie " + billetterieId);
    }
}
