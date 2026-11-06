package net.franzka.sgilt.core.evenement.page.service;

import net.franzka.sgilt.core.evenement.page.domain.PublicPageStatus;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieOpenWithoutPublishedPageException;
import net.franzka.sgilt.core.evenement.billetterie.exception.CapacityBelowSoldTicketsException;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPageDto;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPublicPageDto;
import net.franzka.sgilt.core.evenement.page.exception.PageNotCreatedException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugLockedException;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
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
     * @throws PageEvenementNotFoundException si aucune page en ligne ne correspond au slug
     */
    public EventPageDto getPublicPage(String slug) {
        PageEvenement page = getOnlinePage(slug);
        TicketingDto ticketing = billetterieService.getPublicTicketing(page.getEvenement().getId()).orElse(null);
        return toEventPageDto(page, ticketing);
    }

    /**
     * Retourne l'aperçu de la page publique d'un événement, pour son organisateur : la page telle
     * que la verront les visiteurs, publiée ou non, sans billetterie (pas d'achat depuis un aperçu).
     *
     * @param evenement l'événement, dont le back-office a activé la page publique
     * @return la page, comme sur /e/{slug}
     * @throws PageNotCreatedException si l'organisateur n'a pas encore créé la page
     */
    public EventPageDto getPreview(Evenement evenement) {
        return toEventPageDto(getCreatedPage(evenement), null);
    }

    /**
     * Retourne les infos d'un événement : le tag et le sous-titre de sa page, sa date, ses horaires,
     * son lieu, et les infos pratiques saisies sur sa page (ni tag, ni sous-titre, ni infos pratiques
     * s'il n'a pas de page).
     *
     * @param evenement l'événement
     * @return les infos de l'événement
     */
    public EvenementInfosDto getEvenementInfos(Evenement evenement) {
        PageEvenement page = pageEvenementRepository.findByEvenementId(evenement.getId()).orElse(null);
        LocalDateTime startsAt = startsAt(evenement);
        return pageEvenementMapper.toEvenementInfosDto(evenement, page, startsAt, endsAt(evenement, startsAt));
    }

    /**
     * Retourne l'identifiant de l'événement d'une page publique.
     *
     * @param slug le slug de la page (/e/{slug})
     * @return l'identifiant de l'événement
     * @throws PageEvenementNotFoundException si aucune page en ligne ne correspond au slug
     */
    public UUID getEvenementIdBySlug(String slug) {
        return getOnlinePage(slug).getEvenement().getId();
    }

    /**
     * Retourne la page publique d'un événement telle que l'administre son organisateur : sa page,
     * et sa billetterie avec ses ventes si le back-office l'a activée.
     *
     * @param evenement l'événement, dont le back-office a activé la page publique
     * @return la page publique de l'organisateur
     */
    public OrganizerPublicPageDto getOrganizerPublicPage(Evenement evenement) {
        OrganizerPageDto page = pageEvenementRepository.findByEvenementId(evenement.getId())
                .map(pageEvenementMapper::toOrganizerPageDto)
                .orElse(null);
        return new OrganizerPublicPageDto(
                evenement.isBilletterieEnabled(),
                page,
                billetterieService.getOrganizerTicketing(evenement.getId()).orElse(null),
                billetterieService.getSales(evenement.getId()).orElse(null));
    }

    /**
     * Enregistre le contenu de la page d'un événement, saisi par son organisateur. Le premier
     * enregistrement crée la page, en brouillon.
     *
     * @param evenement l'événement, dont le back-office a activé la page publique
     * @param content   le contenu saisi
     * @throws PageSlugAlreadyUsedException si le slug est celui de la page d'un autre événement
     * @throws PageSlugLockedException      si le slug change alors que la page a déjà été publiée
     */
    public void putOrganizerContent(Evenement evenement, PageContentDto content) {
        Optional<PageEvenement> existing = pageEvenementRepository.findByEvenementId(evenement.getId());
        existing.filter(page -> page.getFirstPublishedAt() != null && !page.getSlug().equals(content.slug()))
                .ifPresent(page -> { throw new PageSlugLockedException(evenement.getId()); });
        if (!isSlugAvailable(content.slug(), evenement)) throw new PageSlugAlreadyUsedException(content.slug());
        PageEvenement pageEvenement = existing
                .map(value -> applyPage(value, content))
                .orElseGet(() -> pageEvenementMapper.toPageEvenement(content, evenement));
        pageEvenementRepository.save(pageEvenement);
    }

    /**
     * Publie la page d'un événement : elle est en ligne sur /e/{slug}. La première publication
     * verrouille le slug. Republier une page publiée ne change rien.
     *
     * @param evenement l'événement, dont le back-office a activé la page publique
     * @throws PageNotCreatedException si l'organisateur n'a pas encore créé la page
     */
    public void publish(Evenement evenement) {
        PageEvenement page = getCreatedPage(evenement);
        if (page.getPublishedAt() != null) return;
        markPublished(page);
        pageEvenementRepository.save(page);
    }

    /**
     * Dépublie la page d'un événement : elle redevient un brouillon, introuvable sur /e/{slug}, et
     * plus rien ne s'y vend. Le statut de sa billetterie ne change pas ; le slug reste verrouillé.
     *
     * @param evenement l'événement, dont le back-office a activé la page publique
     * @throws PageNotCreatedException si l'organisateur n'a pas encore créé la page
     */
    public void unpublish(Evenement evenement) {
        PageEvenement page = getCreatedPage(evenement);
        page.setPublishedAt(null);
        pageEvenementRepository.save(page);
    }

    /**
     * Enregistre les réglages de la billetterie d'un événement, saisis par son organisateur.
     *
     * @param evenement l'événement, dont le back-office a activé la page publique
     * @param ticketing les réglages saisis
     * @throws BilletterieNotFoundException                  si le back-office n'a pas activé la billetterie
     * @throws BilletterieOpenWithoutPublishedPageException si on l'ouvre alors que la page n'est pas publiée
     * @throws CapacityBelowSoldTicketsException             si la jauge passe sous les places vendues
     */
    public void putOrganizerTicketing(Evenement evenement, TicketingSettingsDto ticketing) {
        boolean pagePublished = pageEvenementRepository.findByEvenementId(evenement.getId())
                .map(page -> page.getPublishedAt() != null)
                .orElse(false);
        billetterieService.putOrganizerTicketing(evenement, ticketing, pagePublished);
    }

    /**
     * Retourne l'état de la page publique d'un événement, pour son organisateur.
     *
     * @param evenement l'événement
     * @return pas encore créée, brouillon ou publiée ; null si le back-office ne l'a pas activée
     */
    public PublicPageStatus getPublicPageStatus(Evenement evenement) {
        if (!evenement.isPagePubliqueEnabled()) return null;
        return pageEvenementRepository.findByEvenementId(evenement.getId())
                .map(page -> page.getPublishedAt() != null ? PublicPageStatus.PUBLISHED : PublicPageStatus.DRAFT)
                .orElse(PublicPageStatus.NOT_CREATED);
    }

    /**
     * Indique si la page d'un événement est en ligne : publiée, et page publique activée par le
     * back-office.
     *
     * @param evenement l'événement
     * @return false si l'événement n'a pas de page, ou qu'elle est hors ligne
     */
    public boolean isOnline(Evenement evenement) {
        return pageEvenementRepository.findByEvenementId(evenement.getId()).filter(this::isOnline).isPresent();
    }

    /**
     * Retourne le slug de la page d'un événement (/e/{slug}).
     *
     * @param evenement l'événement
     * @return le slug, ou vide si l'événement n'a pas de page
     */
    public Optional<String> getSlug(Evenement evenement) {
        return pageEvenementRepository.findByEvenementId(evenement.getId()).map(PageEvenement::getSlug);
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

    // Met la page en ligne maintenant ; la première fois, verrouille son slug.
    private PageEvenement markPublished(PageEvenement pageEvenement) {
        LocalDateTime now = LocalDateTime.now();
        pageEvenement.setPublishedAt(now);
        if (pageEvenement.getFirstPublishedAt() == null) pageEvenement.setFirstPublishedAt(now);
        return pageEvenement;
    }

    // Page telle qu'affichée sur /e/{slug}, horaires calculés depuis l'événement.
    private EventPageDto toEventPageDto(PageEvenement page, TicketingDto ticketing) {
        LocalDateTime startsAt = startsAt(page.getEvenement());
        return pageEvenementMapper.toEventPageDto(page, ticketing, startsAt, endsAt(page.getEvenement(), startsAt));
    }

    // Page de l'événement, que l'organisateur doit avoir créée.
    private PageEvenement getCreatedPage(Evenement evenement) {
        return pageEvenementRepository.findByEvenementId(evenement.getId())
                .orElseThrow(() -> new PageNotCreatedException(evenement.getId()));
    }

    // En ligne : publiée, et page publique de son événement activée par le back-office.
    private boolean isOnline(PageEvenement page) {
        return page.getPublishedAt() != null && page.getEvenement().isPagePubliqueEnabled();
    }

    /**
     * Charge une page en ligne : publiée, et page publique de son événement activée par le
     * back-office. Un brouillon ou une page désactivée est introuvable pour le public.
     *
     * @param slug le slug de la page (/e/{slug})
     * @return la page
     * @throws PageEvenementNotFoundException si aucune page en ligne ne correspond au slug
     */
    private PageEvenement getOnlinePage(String slug) {
        return pageEvenementRepository.findBySlug(slug)
                .filter(this::isOnline)
                .orElseThrow(() -> new PageEvenementNotFoundException(slug));
    }

    /**
     * Remplace les champs d'une page existante par sa saisie, une liste absente devenant vide.
     *
     * @param pageEvenement la page existante
     * @param page          la page saisie
     * @return la page mise à jour
     */
    private PageEvenement applyPage(PageEvenement pageEvenement, PageContentDto page) {
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
