package net.franzka.sgilt.core.evenement.billetterie.exception;

import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;

import java.util.UUID;

/**
 * Exception levée lorsqu'on commande des billets sur une billetterie qui n'est pas ouverte.
 */
public class BilletterieNotOpenException extends RuntimeException {

    /**
     * Construit l'exception pour la billetterie concernée.
     *
     * @param billetterieId l'identifiant de la billetterie
     * @param status        son statut
     */
    public BilletterieNotOpenException(UUID billetterieId, BilletterieStatus status) {
        super("Billetterie " + billetterieId + " non ouverte (" + status + ")");
    }
}
