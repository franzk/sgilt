package net.franzka.sgilt.core.evenement.billetterie.dto;

/**
 * Ventes d'une billetterie, pour son organisateur.
 *
 * @param soldTickets    les places vendues (commandes payées)
 * @param heldTickets    les places bloquées par un paiement en cours (commandes en attente pas encore
 *                       expirées)
 * @param capacity       la jauge
 * @param enteredTickets les places entrées (commandes payées dont l'e-billet a été scanné)
 */
public record TicketingSalesDto(
        int soldTickets,
        int heldTickets,
        int capacity,
        int enteredTickets
) {}
