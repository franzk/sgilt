package net.franzka.sgilt.core.evenement.page.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.mapper.PageEvenementMapper;
import net.franzka.sgilt.core.evenement.page.repository.PageEvenementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service métier pour l'entité {@link PageEvenement}.
 */
@Service
@RequiredArgsConstructor
public class PageEvenementService {

    private final PageEvenementRepository pageEvenementRepository;
    private final BilletterieService      billetterieService;
    private final PageEvenementMapper     pageEvenementMapper;

    /**
     * Retourne la page publique d'un événement, avec l'événement et sa billetterie.
     *
     * @param slug le slug de la page (/e/{slug})
     * @return la page publique
     * @throws PageEvenementNotFoundException si aucune page ne correspond au slug
     */
    public EventPageDto getPublicPage(String slug) {
        PageEvenement page = pageEvenementRepository.findBySlug(slug)
                .orElseThrow(() -> new PageEvenementNotFoundException(slug));
        Evenement evenement = page.getEvenement();
        TicketingDto ticketing = billetterieService.getPublicTicketing(evenement.getId()).orElse(null);
        LocalDateTime startsAt = startsAt(evenement);
        return pageEvenementMapper.toEventPageDto(page, ticketing, startsAt, endsAt(evenement, startsAt));
    }

    // Le début, le jour de l'événement ; null s'il n'a pas d'heure de début.
    private LocalDateTime startsAt(Evenement evenement) {
        return evenement.getStartTime() == null ? null : evenement.getDate().atTime(evenement.getStartTime());
    }

    // La fin, le jour de l'événement, ou le lendemain si elle tombe avant le début (ex. 19h – 2h) ;
    // null s'il n'a pas d'heure de fin.
    private LocalDateTime endsAt(Evenement evenement, LocalDateTime startsAt) {
        if (evenement.getEndTime() == null) return null;
        LocalDateTime endsAt = evenement.getDate().atTime(evenement.getEndTime());
        return startsAt != null && endsAt.isBefore(startsAt) ? endsAt.plusDays(1) : endsAt;
    }
}
