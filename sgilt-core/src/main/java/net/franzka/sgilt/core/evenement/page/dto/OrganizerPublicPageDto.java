package net.franzka.sgilt.core.evenement.page.dto;

import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSalesDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;

/**
 * Page publique d'un événement telle que l'administre son organisateur dans son board : sa page,
 * sa billetterie et ses ventes. N'existe que si le back-office a activé la page publique.
 *
 * @param billetterieEnabled interrupteur du back-office : la billetterie est activée
 * @param page               la page, ou null tant que l'organisateur ne l'a pas créée
 * @param ticketing          les réglages de la billetterie, ou null si elle est désactivée ou pas
 *                           encore créée
 * @param sales              les ventes, ou null comme la billetterie
 */
public record OrganizerPublicPageDto(
        boolean billetterieEnabled,
        OrganizerPageDto page,
        TicketingSettingsDto ticketing,
        TicketingSalesDto sales
) {}
