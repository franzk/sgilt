package net.franzka.sgilt.core.evenement.billetterie.commande.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.repository.CommandeRepository;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier pour l'entité {@link Commande}.
 */
@Service
@RequiredArgsConstructor
public class CommandeService {

    // Durée de blocage des places d'une commande en attente de paiement.
    static final Duration COMMANDE_HOLD_DURATION = Duration.ofMinutes(15);

    private final CommandeRepository commandeRepository;
    private final CommandeMapper     commandeMapper;

    /**
     * Retourne le nombre de places restantes d'une billetterie : sa jauge, moins les billets des
     * commandes payées et des commandes en attente pas encore expirées. Jamais négatif, même si la
     * jauge est dépassée.
     *
     * @param billetterie la billetterie
     * @return le nombre de places restantes
     */
    public int getRemainingTickets(Billetterie billetterie) {
        return Math.max(0, billetterie.getCapacity() - takenTickets(billetterie));
    }

    /**
     * Crée une commande en attente de paiement, qui bloque ses places pendant
     * {@link #COMMANDE_HOLD_DURATION}. La billetterie doit avoir été verrouillée par l'appelant :
     * deux commandes simultanées ne peuvent pas prendre les mêmes places.
     *
     * @param billetterie la billetterie, verrouillée et ouverte
     * @param request     la commande demandée
     * @return la commande créée
     * @throws NotEnoughTicketsException s'il ne reste pas assez de places
     */
    public CommandeDto createCommande(Billetterie billetterie, CreateCommandeRequest request) {
        int remainingTickets = getRemainingTickets(billetterie);
        if (request.quantity() > remainingTickets) {
            throw new NotEnoughTicketsException(billetterie.getId(), request.quantity(), remainingTickets);
        }
        LocalDateTime expiresAt = LocalDateTime.now().plus(COMMANDE_HOLD_DURATION);
        Commande commande = commandeRepository.save(commandeMapper.toCommande(request, billetterie, expiresAt));
        return commandeMapper.toCommandeDto(commande);
    }

    // Billets pris : ceux des commandes payées et des commandes en attente pas encore expirées.
    private int takenTickets(Billetterie billetterie) {
        List<Commande> paid = commandeRepository.findByBilletterieIdAndStatus(
                billetterie.getId(), CommandeStatus.PAID);
        List<Commande> held = commandeRepository.findByBilletterieIdAndStatusAndExpiresAtAfter(
                billetterie.getId(), CommandeStatus.PENDING, LocalDateTime.now());
        return sumQuantities(paid) + sumQuantities(held);
    }

    private static int sumQuantities(List<Commande> commandes) {
        return commandes.stream().mapToInt(Commande::getQuantity).sum();
    }
}
