package net.franzka.sgilt.core.evenement.billetterie.commande.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'une commande demande plus de places qu'il n'en reste.
 */
public class NotEnoughTicketsException extends RuntimeException {

    /**
     * Construit l'exception pour la billetterie concernée.
     *
     * @param billetterieId    l'identifiant de la billetterie
     * @param quantity         le nombre de places demandées
     * @param remainingTickets le nombre de places restantes
     */
    public NotEnoughTicketsException(UUID billetterieId, int quantity, int remainingTickets) {
        super("Billetterie " + billetterieId + " : " + quantity + " billet(s) demandé(s), "
                + remainingTickets + " place(s) restante(s)");
    }
}
