package net.franzka.sgilt.core.evenement.billetterie.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;

/**
 * Réglages de la billetterie d'un événement, tels que les saisit son organisateur.
 *
 * @param status         le statut de la billetterie
 * @param unitPriceCents le prix d'un billet, en centimes d'euro
 * @param capacity       le nombre total de places mises en vente
 */
public record TicketingSettingsDto(
        @NotNull BilletterieStatus status,
        @NotNull @PositiveOrZero Integer unitPriceCents,
        @NotNull @PositiveOrZero Integer capacity
) {}
