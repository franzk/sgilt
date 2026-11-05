package net.franzka.sgilt.core.evenement.page.controller;

import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieOpenWithoutPublishedPageException;
import net.franzka.sgilt.core.evenement.billetterie.exception.CapacityBelowSoldTicketsException;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.api.OrganizerPageApi;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPublicPageDto;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import net.franzka.sgilt.core.evenement.page.exception.PageNotCreatedException;
import net.franzka.sgilt.core.evenement.page.exception.PagePubliqueDisabledException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugAlreadyUsedException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugLockedException;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.security.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller HTTP de la page publique d'un événement administrée par son organisateur, dans son
 * board : contenu, publication, billetterie et ventes. Réservé au propriétaire de l'événement, et
 * seulement si le back-office a activé sa page publique. Chaque modification renvoie tout l'état,
 * comme le GET.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_USER')")
public class OrganizerPageController implements OrganizerPageApi {

    private final EvenementService     evenementService;
    private final PageEvenementService pageEvenementService;
    private final CurrentUserService   currentUserService;

    /**
     * Retourne la page publique d'un événement, sa billetterie et ses ventes.
     *
     * @param eventId l'identifiant de l'événement
     * @return la page publique de l'organisateur
     * @throws EvenementNotFoundException    si l'événement n'existe pas
     * @throws EvenementNotAllowedException  si l'événement n'appartient pas à l'utilisateur connecté
     * @throws PagePubliqueDisabledException si le back-office n'a pas activé sa page publique
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<OrganizerPublicPageDto> getPublicPage(UUID eventId) {
        log.info("GET /user/events/{}/page-publique", eventId);
        return ResponseEntity.ok(pageEvenementService.getOrganizerPublicPage(ownedEvent(eventId)));
    }

    /**
     * Retourne l'aperçu de la page : telle que la verront les visiteurs, publiée ou non, sans
     * billetterie.
     *
     * @param eventId l'identifiant de l'événement
     * @return la page, comme sur /e/{slug}
     * @throws EvenementNotFoundException    si l'événement n'existe pas
     * @throws EvenementNotAllowedException  si l'événement n'appartient pas à l'utilisateur connecté
     * @throws PagePubliqueDisabledException si le back-office n'a pas activé sa page publique
     * @throws PageNotCreatedException       si l'organisateur n'a pas encore créé la page
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventPageDto> getPreview(UUID eventId) {
        log.info("GET /user/events/{}/page-publique/apercu", eventId);
        return ResponseEntity.ok(pageEvenementService.getPreview(ownedEvent(eventId)));
    }

    /**
     * Enregistre le contenu de la page ; le premier enregistrement la crée, en brouillon.
     *
     * @param eventId l'identifiant de l'événement
     * @param content le contenu saisi
     * @return la page publique de l'organisateur, à jour
     * @throws EvenementNotFoundException    si l'événement n'existe pas
     * @throws EvenementNotAllowedException  si l'événement n'appartient pas à l'utilisateur connecté
     * @throws PagePubliqueDisabledException si le back-office n'a pas activé sa page publique
     * @throws PageSlugAlreadyUsedException  si le slug est celui de la page d'un autre événement
     * @throws PageSlugLockedException       si le slug change alors que la page a déjà été publiée
     */
    @Override
    @Transactional
    public ResponseEntity<OrganizerPublicPageDto> putContent(UUID eventId, PageContentDto content) {
        log.info("PUT /user/events/{}/page-publique/contenu", eventId);
        Evenement evenement = ownedEvent(eventId);
        pageEvenementService.putOrganizerContent(evenement, content);
        return ResponseEntity.ok(pageEvenementService.getOrganizerPublicPage(evenement));
    }

    /**
     * Publie la page : elle est en ligne sur /e/{slug}, et son slug est verrouillé.
     *
     * @param eventId l'identifiant de l'événement
     * @return la page publique de l'organisateur, à jour
     * @throws EvenementNotFoundException    si l'événement n'existe pas
     * @throws EvenementNotAllowedException  si l'événement n'appartient pas à l'utilisateur connecté
     * @throws PagePubliqueDisabledException si le back-office n'a pas activé sa page publique
     * @throws PageNotCreatedException       si l'organisateur n'a pas encore créé la page
     */
    @Override
    @Transactional
    public ResponseEntity<OrganizerPublicPageDto> publish(UUID eventId) {
        log.info("POST /user/events/{}/page-publique/publier", eventId);
        Evenement evenement = ownedEvent(eventId);
        pageEvenementService.publish(evenement);
        return ResponseEntity.ok(pageEvenementService.getOrganizerPublicPage(evenement));
    }

    /**
     * Dépublie la page : elle redevient un brouillon, introuvable, et plus rien ne s'y vend.
     *
     * @param eventId l'identifiant de l'événement
     * @return la page publique de l'organisateur, à jour
     * @throws EvenementNotFoundException    si l'événement n'existe pas
     * @throws EvenementNotAllowedException  si l'événement n'appartient pas à l'utilisateur connecté
     * @throws PagePubliqueDisabledException si le back-office n'a pas activé sa page publique
     * @throws PageNotCreatedException       si l'organisateur n'a pas encore créé la page
     */
    @Override
    @Transactional
    public ResponseEntity<OrganizerPublicPageDto> unpublish(UUID eventId) {
        log.info("POST /user/events/{}/page-publique/depublier", eventId);
        Evenement evenement = ownedEvent(eventId);
        pageEvenementService.unpublish(evenement);
        return ResponseEntity.ok(pageEvenementService.getOrganizerPublicPage(evenement));
    }

    /**
     * Enregistre les réglages de la billetterie ; le premier enregistrement la crée.
     *
     * @param eventId   l'identifiant de l'événement
     * @param ticketing les réglages saisis
     * @return la page publique de l'organisateur, à jour
     * @throws EvenementNotFoundException                    si l'événement n'existe pas
     * @throws EvenementNotAllowedException                  si l'événement n'appartient pas à l'utilisateur connecté
     * @throws PagePubliqueDisabledException                 si le back-office n'a pas activé sa page publique
     * @throws BilletterieNotFoundException                  si le back-office n'a pas activé la billetterie
     * @throws BilletterieOpenWithoutPublishedPageException si on l'ouvre alors que la page n'est pas publiée
     * @throws CapacityBelowSoldTicketsException             si la jauge passe sous les places vendues
     */
    @Override
    @Transactional
    public ResponseEntity<OrganizerPublicPageDto> putTicketing(UUID eventId, TicketingSettingsDto ticketing) {
        log.info("PUT /user/events/{}/page-publique/billetterie", eventId);
        Evenement evenement = ownedEvent(eventId);
        pageEvenementService.putOrganizerTicketing(evenement, ticketing);
        return ResponseEntity.ok(pageEvenementService.getOrganizerPublicPage(evenement));
    }

    // Événement de l'utilisateur connecté, dont le back-office a activé la page publique.
    private Evenement ownedEvent(UUID eventId) {
        return evenementService.getOwnedEventWithPagePublique(eventId, currentUserService.getId());
    }
}
