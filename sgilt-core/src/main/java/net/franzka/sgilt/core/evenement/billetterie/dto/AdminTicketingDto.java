package net.franzka.sgilt.core.evenement.billetterie.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;

/**
 * Billetterie d'un événement telle que la saisit le back-office admin.
 *
 * @param status         le statut de la billetterie
 * @param unitPriceCents le prix d'un billet, en centimes d'euro
 * @param capacity       le nombre total de places mises en vente
 */
public record AdminTicketingDto(
        @NotNull BilletterieStatus status,
        @NotNull @PositiveOrZero Integer unitPriceCents,
        @NotNull @PositiveOrZero Integer capacity
) {}
