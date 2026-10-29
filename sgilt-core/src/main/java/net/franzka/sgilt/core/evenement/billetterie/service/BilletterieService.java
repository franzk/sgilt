package net.franzka.sgilt.core.evenement.billetterie.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
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
     * @return la billetterie, ou vide si l'événement n'en a pas
     */
    public Optional<TicketingDto> getPublicTicketing(UUID evenementId) {
        return billetterieRepository.findByEvenementId(evenementId)
                .map(billetterie -> billetterieMapper.toTicketingDto(billetterie, commandeService.getRemainingTickets(billetterie)));
    }

    /**
     * Retourne la billetterie d'un événement telle que la saisit le back-office admin.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, ou vide si l'événement n'en a pas
     */
    public Optional<AdminTicketingDto> getAdminTicketing(UUID evenementId) {
        return billetterieRepository.findByEvenementId(evenementId).map(billetterieMapper::toAdminTicketingDto);
    }

    /**
     * Verrouille la billetterie ouverte d'un événement pour y commander des billets : jusqu'à la
     * fin de la transaction, aucune autre commande ne peut y prendre de places.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, verrouillée
     * @throws BilletterieNotFoundException si l'événement n'a pas de billetterie
     * @throws BilletterieNotOpenException  si la billetterie n'est pas ouverte
     */
    public Billetterie lockOpenBilletterie(UUID evenementId) {
        Billetterie billetterie = billetterieRepository.findForUpdateByEvenementId(evenementId)
                .orElseThrow(() -> new BilletterieNotFoundException(evenementId));
        if (billetterie.getStatus() != BilletterieStatus.OPEN) {
            throw new BilletterieNotOpenException(billetterie.getId(), billetterie.getStatus());
        }
        return billetterie;
    }

    /**
     * Remplace la billetterie d'un événement par sa saisie admin : la crée, la met à jour, ou la
     * supprime si la saisie est null.
     *
     * @param evenement l'événement
     * @param ticketing la billetterie saisie, ou null pour qu'il n'en ait plus
     */
    public void putTicketing(Evenement evenement, AdminTicketingDto ticketing) {
        Optional<Billetterie> existing = billetterieRepository.findByEvenementId(evenement.getId());
        if (ticketing == null) {
            existing.ifPresent(billetterieRepository::delete);
            return;
        }
        Billetterie billetterie = existing
                .map(value -> applyTicketing(value, ticketing))
                .orElseGet(() -> billetterieMapper.toBilletterie(ticketing, evenement));
        billetterieRepository.save(billetterie);
    }

    // Remplace les champs d'une billetterie existante par sa saisie.
    private Billetterie applyTicketing(Billetterie billetterie, AdminTicketingDto ticketing) {
        billetterie.setStatus(ticketing.status());
        billetterie.setUnitPriceCents(ticketing.unitPriceCents());
        billetterie.setCapacity(ticketing.capacity());
        return billetterie;
    }
}
