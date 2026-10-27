package net.franzka.sgilt.core.evenement.billetterie.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
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

    /**
     * Retourne la billetterie d'un événement telle que la voit le public.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la billetterie, ou vide si l'événement n'en a pas
     */
    public Optional<TicketingDto> getPublicTicketing(UUID evenementId) {
        return billetterieRepository.findByEvenementId(evenementId)
                .map(billetterie -> billetterieMapper.toTicketingDto(billetterie, remainingTickets(billetterie)));
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

    // Pas encore de commandes : toute la jauge reste disponible.
    private int remainingTickets(Billetterie billetterie) {
        return billetterie.getCapacity();
    }
}
