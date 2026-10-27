package net.franzka.sgilt.core.evenement.billetterie.dto;

import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;

/**
 * Billetterie d'un événement telle que la voit le public (page /e/{slug}).
 *
 * @param status           le statut de la billetterie (« complet » s'en déduit : ouverte sans place)
 * @param unitPriceCents   le prix d'un billet, en centimes d'euro
 * @param remainingTickets le nombre de places restantes
 */
public record TicketingDto(BilletterieStatus status, int unitPriceCents, int remainingTickets) {}
