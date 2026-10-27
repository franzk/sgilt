package net.franzka.sgilt.core.evenement.page.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.AdminPageDto;
import net.franzka.sgilt.core.evenement.page.dto.AdminEventPublicFormDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.exception.BilletterieWithoutPageException;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugAlreadyUsedException;
import net.franzka.sgilt.core.evenement.page.mapper.PageEvenementMapper;
import net.franzka.sgilt.core.evenement.page.repository.PageEvenementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Retourne la face publique d'un événement telle que la saisit le back-office admin : ses
     * horaires, sa page et sa billetterie.
     *
     * @param evenement l'événement
     * @return la face publique, page et billetterie à null si l'événement n'en a pas
     */
    public AdminEventPublicFormDto getAdminPublicPage(Evenement evenement) {
        PageEvenement page = pageEvenementRepository.findByEvenementId(evenement.getId()).orElse(null);
        AdminTicketingDto ticketing = billetterieService.getAdminTicketing(evenement.getId()).orElse(null);
        return pageEvenementMapper.toAdminEventPublicFormDto(evenement, page, ticketing);
    }

    /**
     * Remplace la page et la billetterie d'un événement par leur saisie admin : chacune est créée,
     * mise à jour, ou supprimée si sa saisie est null.
     *
     * @param evenement l'événement
     * @param page      la page saisie, ou null pour qu'il n'en ait plus
     * @param ticketing la billetterie saisie, ou null pour qu'il n'en ait plus
     * @throws BilletterieWithoutPageException si une billetterie est saisie sans page
     * @throws PageSlugAlreadyUsedException    si le slug est celui de la page d'un autre événement
     */
    public void putPublicPage(Evenement evenement, AdminPageDto page, AdminTicketingDto ticketing) {
        if (page == null && ticketing != null) throw new BilletterieWithoutPageException(evenement.getId());
        putPage(evenement, page);
        billetterieService.putTicketing(evenement, ticketing);
    }

    /**
     * Retourne le slug de la page de chaque événement qui en a une.
     *
     * @return les slugs, par identifiant d'événement
     */
    public Map<UUID, String> getSlugsByEvenementId() {
        return pageEvenementRepository.findAll().stream()
                .collect(Collectors.toMap(page -> page.getEvenement().getId(), PageEvenement::getSlug));
    }

    private void putPage(Evenement evenement, AdminPageDto page) {
        Optional<PageEvenement> existing = pageEvenementRepository.findByEvenementId(evenement.getId());
        if (page == null) {
            existing.ifPresent(pageEvenementRepository::delete);
            return;
        }
        requireSlugAvailable(page.slug(), evenement);
        PageEvenement pageEvenement = existing
                .map(value -> applyPage(value, page))
                .orElseGet(() -> pageEvenementMapper.toPageEvenement(page, evenement));
        pageEvenementRepository.save(pageEvenement);
    }

    // Remplace les champs d'une page existante par sa saisie ; une liste absente est vide.
    private PageEvenement applyPage(PageEvenement pageEvenement, AdminPageDto page) {
        pageEvenement.setSlug(page.slug());
        pageEvenement.setTag(page.tag());
        pageEvenement.setSubtitle(page.subtitle());
        pageEvenement.setSectionTitle(page.sectionTitle());
        pageEvenement.setDescription(page.description());
        pageEvenement.setImportantInfo(Objects.requireNonNullElse(page.importantInfo(), List.of()));
        pageEvenement.setPracticalInfo(Objects.requireNonNullElse(page.practicalInfo(), List.of()));
        pageEvenement.setPictos(Objects.requireNonNullElse(page.pictos(), List.of()));
        return pageEvenement;
    }

    // Le slug est unique : il peut être celui de la page de cet événement, pas d'un autre.
    private void requireSlugAvailable(String slug, Evenement evenement) {
        pageEvenementRepository.findBySlug(slug)
                .filter(other -> !other.getEvenement().getId().equals(evenement.getId()))
                .ifPresent(other -> {
                    throw new PageSlugAlreadyUsedException(slug);
                });
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
