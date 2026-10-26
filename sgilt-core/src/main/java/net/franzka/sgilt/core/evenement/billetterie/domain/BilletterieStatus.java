package net.franzka.sgilt.core.evenement.billetterie.domain;

/**
 * Statut d'une billetterie, piloté par l'organisateur ; une fois l'événement passé, elle est
 * fermée d'office. « Complet » n'en est pas un : il se déduit des places restantes.
 */
public enum BilletterieStatus {
    NOT_OPEN,
    OPEN,
    CLOSED
}
