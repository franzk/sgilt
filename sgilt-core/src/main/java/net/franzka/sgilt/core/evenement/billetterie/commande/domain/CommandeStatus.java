package net.franzka.sgilt.core.evenement.billetterie.commande.domain;

/**
 * Statut d'une commande de billets : PENDING à la création (places bloquées jusqu'à son
 * expiration), PAID au paiement, CANCELLED si elle est annulée.
 */
public enum CommandeStatus {
    PENDING,
    PAID,
    CANCELLED
}
