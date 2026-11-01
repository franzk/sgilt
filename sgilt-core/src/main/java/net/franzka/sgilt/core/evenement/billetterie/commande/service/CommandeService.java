package net.franzka.sgilt.core.evenement.billetterie.commande.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.repository.CommandeRepository;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.pay.PayClient;
import net.franzka.sgilt.core.pay.PaymentIntentRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Commande}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeService {

    // Durée de blocage des places d'une commande en attente de paiement.
    static final Duration COMMANDE_HOLD_DURATION = Duration.ofMinutes(10);

    private final CommandeRepository commandeRepository;
    private final CommandeMapper     commandeMapper;
    private final PayClient          payClient;

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
     * {@link #COMMANDE_HOLD_DURATION}, et son PaymentIntent Stripe (via sgilt-pay). La billetterie
     * doit avoir été verrouillée par l'appelant : deux commandes simultanées ne peuvent pas prendre
     * les mêmes places. Si sgilt-pay échoue, l'exception annule la transaction, donc la commande.
     *
     * @param billetterie la billetterie, verrouillée et ouverte
     * @param request     la commande demandée
     * @return la commande créée, avec le secret client de son PaymentIntent
     * @throws NotEnoughTicketsException s'il ne reste pas assez de places
     */
    public CommandeDto createCommande(Billetterie billetterie, CreateCommandeRequest request) {
        int remainingTickets = getRemainingTickets(billetterie);
        if (request.quantity() > remainingTickets) {
            throw new NotEnoughTicketsException(billetterie.getId(), request.quantity(), remainingTickets);
        }
        LocalDateTime expiresAt = LocalDateTime.now().plus(COMMANDE_HOLD_DURATION);
        Commande commande = commandeRepository.save(commandeMapper.toCommande(request, billetterie, expiresAt));
        return commandeMapper.toCommandeDto(commande, createPaymentIntent(commande));
    }

    /**
     * Relit une commande, pour l'écran de confirmation.
     *
     * @param commandeId l'identifiant de la commande
     * @return la commande, avec son statut et son montant total
     * @throws CommandeNotFoundException si la commande est introuvable
     */
    public CommandeSummaryDto getCommande(UUID commandeId) {
        return commandeMapper.toCommandeSummaryDto(findCommande(commandeId));
    }

    /**
     * Marque une commande payée, à la réception du paiement confirmé par Stripe. Idempotent : Stripe
     * peut envoyer le même événement plusieurs fois, une commande déjà payée reste telle quelle. Une
     * commande expirée est marquée payée elle aussi : le paiement a bien eu lieu.
     *
     * @param commandeId l'identifiant de la commande
     * @throws CommandeNotFoundException si la commande est introuvable
     */
    public void markPaid(UUID commandeId) {
        Commande commande = findCommande(commandeId);
        if (commande.getStatus() == CommandeStatus.PAID) {
            log.info("Commande déjà payée, rien à faire — commandeId={}", commandeId);
            return;
        }
        commande.setStatus(CommandeStatus.PAID);
        commandeRepository.save(commande);
    }

    private Commande findCommande(UUID commandeId) {
        return commandeRepository.findById(commandeId).orElseThrow(() -> new CommandeNotFoundException(commandeId));
    }

    // Crée le PaymentIntent de la commande, au montant de la commande ; renvoie son secret client.
    private String createPaymentIntent(Commande commande) {
        int amountCents = commande.getQuantity() * commande.getUnitPriceCents();
        return payClient.createPaymentIntent(new PaymentIntentRequest(commande.getId(), amountCents)).clientSecret();
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
