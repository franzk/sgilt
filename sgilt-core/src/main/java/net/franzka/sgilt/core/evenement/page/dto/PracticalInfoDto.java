package net.franzka.sgilt.core.evenement.page.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Infos pratiques d'un événement, montrées à l'acheteur sur son e-billet : quand, où, et
 * les infos pratiques saisies sur sa page.
 *
 * @param date     la date de l'événement
 * @param startsAt le début, en heure locale du lieu, ou null si l'événement n'a pas d'heure de début
 * @param endsAt   la fin, en heure locale du lieu (le lendemain si elle passe minuit), ou null si
 *                 l'événement n'a pas d'heure de fin
 * @param venue    le lieu de l'événement
 * @param city     la ville de l'événement
 * @param lines    les infos pratiques saisies sur la page de l'événement
 */
public record PracticalInfoDto(
        LocalDate date,
        // Même format que EventPageDto ('2026-03-12T19:00'), sans secondes ni fuseau.
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startsAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endsAt,
        String venue,
        String city,
        List<String> lines
) {}
