package net.franzka.sgilt.core.evenement.billetterie.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSalesDto;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieOpenWithoutPublishedPageException;
import net.franzka.sgilt.core.evenement.billetterie.exception.CapacityBelowSoldTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieWithSalesException;
import net.franzka.sgilt.core.evenement.billetterie.mapper.BilletterieMapper;
import net.franzka.sgilt.core.evenement.billetterie.repository.BilletterieRepository;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Billetterie}.
 */
@Service
@RequiredArgsConstructor
public class BilletterieService {

    private final BilletterieRepository billetterieRepository;
    private final BilletterieMapper     billetterieMapper;
    private final CommandeService       commandeService;

    /**
     * Retourne la billetterie d'un événement telle que la voit le public.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, ou vide si l'événement n'en a pas ou qu'elle est désactivée
     */
    public Optional<TicketingDto> getPublicTicketing(UUID evenementId) {
        return billetterieRepository.findByEvenementId(evenementId)
                .filter(this::isEnabled)
                .map(billetterie -> billetterieMapper.toTicketingDto(billetterie, commandeService.getRemainingTickets(billetterie)));
    }

    /**
     * Verrouille la billetterie ouverte d'un événement pour y commander des billets : jusqu'à la
     * fin de la transaction, aucune autre commande ne peut y prendre de places.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, verrouillée
     * @throws BilletterieNotFoundException si l'événement n'a pas de billetterie ou qu'elle est désactivée
     * @throws BilletterieNotOpenException  si la billetterie n'est pas ouverte
     */
    public Billetterie lockOpenBilletterie(UUID evenementId) {
        Billetterie billetterie = billetterieRepository.findForUpdateByEvenementId(evenementId)
                .filter(this::isEnabled)
                .orElseThrow(() -> new BilletterieNotFoundException(evenementId));
        if (billetterie.getStatus() != BilletterieStatus.OPEN) {
            throw new BilletterieNotOpenException(billetterie.getId(), billetterie.getStatus());
        }
        return billetterie;
    }

    /**
     * Retourne les réglages de la billetterie d'un événement, pour son organisateur.
     *
     * @param evenementId l'identifiant de l'événement
     * @return les réglages, ou vide si l'événement n'a pas de billetterie ou qu'elle est désactivée
     */
    public Optional<TicketingSettingsDto> getOrganizerTicketing(UUID evenementId) {
        return billetterieRepository.findByEvenementId(evenementId)
                .filter(this::isEnabled)
                .map(billetterieMapper::toTicketingSettingsDto);
    }

    /**
     * Retourne les ventes de la billetterie d'un événement, pour son organisateur.
     *
     * @param evenementId l'identifiant de l'événement
     * @return les ventes, ou vide si l'événement n'a pas de billetterie ou qu'elle est désactivée
     */
    public Optional<TicketingSalesDto> getSales(UUID evenementId) {
        return billetterieRepository.findByEvenementId(evenementId)
                .filter(this::isEnabled)
                .map(billetterie -> new TicketingSalesDto(
                        commandeService.getSoldTickets(billetterie),
                        commandeService.getHeldTickets(billetterie),
                        billetterie.getCapacity()));
    }

    /**
     * Crée ou met à jour la billetterie d'un événement, saisie par son organisateur. La billetterie
     * est verrouillée pendant la mise à jour : aucune commande ne peut y prendre de places entre la
     * vérification de la jauge et son enregistrement. Le prix reste modifiable après des ventes :
     * chaque commande garde le sien. Sur une page non publiée, la billetterie ne s'ouvre pas ; déjà
     * ouverte (page dépubliée depuis), elle le reste et ses autres réglages s'enregistrent.
     *
     * @param evenement     l'événement
     * @param ticketing     les réglages saisis
     * @param pagePublished la page de l'événement est publiée
     * @throws BilletterieNotFoundException                  si le back-office n'a pas activé la billetterie
     * @throws BilletterieOpenWithoutPublishedPageException si on l'ouvre (elle ne l'était pas) alors que la page n'est pas publiée
     * @throws CapacityBelowSoldTicketsException             si la jauge passe sous les places vendues
     */
    public void putOrganizerTicketing(Evenement evenement, TicketingSettingsDto ticketing, boolean pagePublished) {
        if (!evenement.isBilletterieEnabled()) throw new BilletterieNotFoundException(evenement.getId());
        Optional<Billetterie> existing = billetterieRepository.findForUpdateByEvenementId(evenement.getId());
        boolean alreadyOpen = existing.map(billetterie -> billetterie.getStatus() == BilletterieStatus.OPEN).orElse(false);
        if (ticketing.status() == BilletterieStatus.OPEN && !alreadyOpen && !pagePublished) {
            throw new BilletterieOpenWithoutPublishedPageException(evenement.getId());
        }
        existing.ifPresent(billetterie -> checkCapacityCoversSoldTickets(billetterie, ticketing.capacity()));
        Billetterie billetterie = existing
                .map(value -> applyTicketing(value, ticketing))
                .orElseGet(() -> billetterieMapper.toBilletterie(ticketing, evenement));
        billetterieRepository.save(billetterie);
    }

    // La jauge ne passe jamais sous les places vendues : leurs acheteurs ont des e-billets.
    private void checkCapacityCoversSoldTickets(Billetterie billetterie, int capacity) {
        int soldTickets = commandeService.getSoldTickets(billetterie);
        if (capacity < soldTickets) {
            throw new CapacityBelowSoldTicketsException(billetterie.getId(), capacity, soldTickets);
        }
    }

    /**
     * Vérifie que la billetterie d'un événement peut être désactivée : pas tant qu'elle a des
     * ventes, ses acheteurs ont des e-billets.
     *
     * @param evenementId l'identifiant de l'événement
     * @throws BilletterieWithSalesException si la billetterie a des ventes
     */
    public void checkCanBeDisabled(UUID evenementId) {
        boolean hasSales = billetterieRepository.findByEvenementId(evenementId)
                .map(commandeService::hasSales)
                .orElse(false);
        if (hasSales) throw new BilletterieWithSalesException(evenementId);
    }

    // Billetterie activée par le back-office ; désactivée, elle n'existe pas pour le public.
    private boolean isEnabled(Billetterie billetterie) {
        return billetterie.getEvenement().isBilletterieEnabled();
    }

    // Remplace les champs d'une billetterie existante par sa saisie.
    private Billetterie applyTicketing(Billetterie billetterie, TicketingSettingsDto ticketing) {
        billetterie.setStatus(ticketing.status());
        billetterie.setUnitPriceCents(ticketing.unitPriceCents());
        billetterie.setCapacity(ticketing.capacity());
        return billetterie;
    }
}
