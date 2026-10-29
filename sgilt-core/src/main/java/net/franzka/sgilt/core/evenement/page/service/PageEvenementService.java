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
     * Retourne l'identifiant de l'événement d'une page publique.
     *
     * @param slug le slug de la page (/e/{slug})
     * @return l'identifiant de l'événement
     * @throws PageEvenementNotFoundException si aucune page ne correspond au slug
     */
    public UUID getEvenementIdBySlug(String slug) {
        return pageEvenementRepository.findBySlug(slug)
                .map(page -> page.getEvenement().getId())
                .orElseThrow(() -> new PageEvenementNotFoundException(slug));
    }

    /**
     * Retourne les horaires, la page et la billetterie d'un événement, tels que les saisit l'admin.
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
     * Crée, met à jour ou supprime (saisie null) la page et la billetterie d'un événement.
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

    /**
     * Crée, met à jour ou supprime (saisie null) la page d'un événement.
     *
     * @param evenement l'événement
     * @param page      la page saisie, ou null pour qu'il n'en ait plus
     * @throws PageSlugAlreadyUsedException si le slug est celui de la page d'un autre événement
     */
    private void putPage(Evenement evenement, AdminPageDto page) {
        Optional<PageEvenement> existing = pageEvenementRepository.findByEvenementId(evenement.getId());
        if (page == null) {
            existing.ifPresent(pageEvenementRepository::delete);
            return;
        }
        if (!isSlugAvailable(page.slug(), evenement)) throw new PageSlugAlreadyUsedException(page.slug());
        PageEvenement pageEvenement = existing
                .map(value -> applyPage(value, page))
                .orElseGet(() -> pageEvenementMapper.toPageEvenement(page, evenement));
        pageEvenementRepository.save(pageEvenement);
    }

    /**
     * Remplace les champs d'une page existante par sa saisie, une liste absente devenant vide.
     *
     * @param pageEvenement la page existante
     * @param page          la page saisie
     * @return la page mise à jour
     */
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

    /**
     * Indique si le slug est libre ou déjà celui de la page de cet événement.
     *
     * @param slug      le slug saisi
     * @param evenement l'événement
     * @return false si le slug est celui de la page d'un autre événement
     */
    private boolean isSlugAvailable(String slug, Evenement evenement) {
        return pageEvenementRepository.findBySlug(slug)
                .filter(pageWithSlug -> !pageWithSlug.getEvenement().getId().equals(evenement.getId()))
                .isEmpty();
    }

    /**
     * Calcule le début de l'événement, le jour de l'événement à son heure de début.
     *
     * @param evenement l'événement
     * @return le début, ou null s'il n'a pas d'heure de début
     */
    private LocalDateTime startsAt(Evenement evenement) {
        return evenement.getStartTime() == null ? null : evenement.getDate().atTime(evenement.getStartTime());
    }

    /**
     * Calcule la fin de l'événement, le lendemain si elle tombe avant le début (ex. 19h – 2h).
     *
     * @param evenement l'événement
     * @param startsAt  le début, ou null
     * @return la fin, ou null s'il n'a pas d'heure de fin
     */
    private LocalDateTime endsAt(Evenement evenement, LocalDateTime startsAt) {
        if (evenement.getEndTime() == null) return null;
        LocalDateTime endsAt = evenement.getDate().atTime(evenement.getEndTime());
        return startsAt != null && endsAt.isBefore(startsAt) ? endsAt.plusDays(1) : endsAt;
    }
}
