package net.franzka.sgilt.core.evenement.page.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Infos d'un événement, montrées à l'acheteur sur son e-billet : le bandeau de sa page (tag,
 * sous-titre), quand, où, et les infos pratiques saisies sur sa page.
 *
 * @param tag           le libellé au-dessus du titre, ou null (pas saisi, ou pas de page)
 * @param subtitle      le sous-titre, ou null (pas saisi, ou pas de page)
 * @param date          la date de l'événement
 * @param startsAt      le début, en heure locale du lieu, ou null si l'événement n'a pas d'heure de début
 * @param endsAt        la fin, en heure locale du lieu (le lendemain si elle passe minuit), ou null si
 *                      l'événement n'a pas d'heure de fin
 * @param venue         le lieu de l'événement
 * @param city          la ville de l'événement
 * @param mapsUrl       le lien Google Maps du lieu, ou null (pas de localisation, ou pas de page)
 * @param practicalInfo les infos pratiques saisies sur la page de l'événement (aucune sans page)
 */
public record EvenementInfosDto(
        String tag,
        String subtitle,
        LocalDate date,
        // Même format que EventPageDto ('2026-03-12T19:00'), sans secondes ni fuseau.
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startsAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endsAt,
        String venue,
        String city,
        String mapsUrl,
        List<String> practicalInfo
) {}
