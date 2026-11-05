package net.franzka.sgilt.core.evenement.billetterie.dto;

/**
 * Ventes d'une billetterie, pour son organisateur.
 *
 * @param soldTickets les places vendues (commandes payées)
 * @param heldTickets les places bloquées par un paiement en cours (commandes en attente pas encore
 *                    expirées)
 * @param capacity    la jauge
 */
public record TicketingSalesDto(
        int soldTickets,
        int heldTickets,
        int capacity
) {}
