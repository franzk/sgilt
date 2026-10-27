package net.franzka.sgilt.core.evenement.page.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Page publique d'un événement (/e/{slug}) : l'événement (titre, horaires, image, lieu, ville), sa
 * page et, s'il en a une, sa billetterie.
 *
 * @param slug          le slug de la page
 * @param tag           le libellé libre au-dessus du titre, ou null
 * @param title         le titre de l'événement
 * @param subtitle      le sous-titre, ou null
 * @param heroImage     le chemin de l'image de l'événement, ou null
 * @param date          la date de l'événement
 * @param startsAt      le début, en heure locale du lieu, ou null si l'événement n'a pas d'heure de début
 * @param endsAt        la fin, en heure locale du lieu (le lendemain si elle passe minuit), ou null si
 *                      l'événement n'a pas d'heure de fin
 * @param venue         le lieu de l'événement
 * @param city          la ville de l'événement
 * @param sectionTitle  le titre de la présentation, ou null
 * @param description   le texte de présentation, ou null
 * @param ticketing     la billetterie, ou null si l'événement n'en a pas
 * @param importantInfo les infos importantes, montrées avant l'achat
 * @param pictos        les points forts de la page
 * @param practicalInfo les infos pratiques, montrées après l'achat
 */
public record EventPageDto(
        String slug,
        String tag,
        String title,
        String subtitle,
        String heroImage,
        LocalDate date,
        // Format attendu par le front ('2026-03-12T19:00'), sans secondes ni fuseau.
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startsAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endsAt,
        String venue,
        String city,
        String sectionTitle,
        String description,
        TicketingDto ticketing,
        List<String> importantInfo,
        List<PageEvenementPicto> pictos,
        List<String> practicalInfo
) {}
