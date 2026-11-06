package net.franzka.sgilt.core.evenement.billetterie.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;

/**
 * Réglages de la billetterie d'un événement, tels que les saisit son organisateur.
 *
 * @param status         le statut de la billetterie
 * @param unitPriceCents le prix d'un billet, en centimes d'euro
 * @param priceNote      la mention affichée après le prix (« Tout compris »…), null sans mention
 * @param capacity       le nombre total de places mises en vente
 */
public record TicketingSettingsDto(
        @NotNull BilletterieStatus status,
        @NotNull @PositiveOrZero Integer unitPriceCents,
        @Size(max = 255) String priceNote,
        @NotNull @PositiveOrZero Integer capacity
) {}
