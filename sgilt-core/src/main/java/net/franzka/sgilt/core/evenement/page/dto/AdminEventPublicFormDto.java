package net.franzka.sgilt.core.evenement.page.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;

import java.time.LocalTime;

/**
 * Face publique d'un événement telle que la saisit le back-office admin, lue et remplacée d'un
 * bloc (PUT) : ses horaires, sa page et sa billetterie. Un bloc à null n'existe pas — envoyé à
 * null, il est supprimé.
 *
 * @param startTime l'heure de début de l'événement, ou null
 * @param endTime   l'heure de fin de l'événement (le lendemain si avant le début), ou null
 * @param page      la page de l'événement, ou null s'il n'en a pas
 * @param ticketing la billetterie de l'événement, ou null s'il n'en a pas (exige une page)
 */
public record AdminEventPublicFormDto(
        // Sans secondes ('19:00').
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime,
        @Valid AdminPageDto page,
        @Valid AdminTicketingDto ticketing
) {}
