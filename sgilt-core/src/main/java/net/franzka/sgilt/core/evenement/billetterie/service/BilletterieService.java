package net.franzka.sgilt.core.evenement.billetterie.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.mapper.BilletterieMapper;
import net.franzka.sgilt.core.evenement.billetterie.repository.BilletterieRepository;
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
        return billetterieRepository
                .findByEvenementId(evenementId)
                .map(billetterie ->
                        billetterieMapper.toTicketingDto(billetterie, remainingTickets(billetterie))
                );
    }

    // Pas encore de commandes : toute la jauge reste disponible.
    private int remainingTickets(Billetterie billetterie) {
        return billetterie.getCapacity();
    }
}
