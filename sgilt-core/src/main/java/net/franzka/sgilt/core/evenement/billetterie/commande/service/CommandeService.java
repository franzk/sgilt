package net.franzka.sgilt.core.evenement.billetterie.commande.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.mailer.AdminMailerService;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.EntryCodeUtils;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotPaidException;
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
import java.util.Optional;
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
    private final AdminMailerService adminMailerService;

    /**
     * Retourne le nombre de places restantes d'une billetterie : sa jauge, moins les places des
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
     * Indique si une billetterie a vendu des billets : au moins une commande payée.
     *
     * @param billetterie la billetterie
     * @return true si elle a des ventes
     */
    public boolean hasSales(Billetterie billetterie) {
        return commandeRepository.existsByBilletterieIdAndStatus(billetterie.getId(), CommandeStatus.PAID);
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
     * Retourne une commande payée, pour son e-billet.
     *
     * @param commandeId l'identifiant de la commande
     * @return la commande payée
     * @throws CommandeNotFoundException si la commande est introuvable
     * @throws CommandeNotPaidException  si la commande n'est pas payée
     */
    public Commande getPaidCommande(UUID commandeId) {
        Commande commande = findCommande(commandeId);
        if (commande.getStatus() != CommandeStatus.PAID) {
            throw new CommandeNotPaidException(commandeId);
        }
        return commande;
    }

    /**
     * Marque une commande payée, à la réception du paiement confirmé par Stripe. Idempotent : Stripe
     * peut envoyer le même événement plusieurs fois, une commande déjà payée reste telle quelle. Une
     * commande expirée est marquée payée elle aussi : le paiement a bien eu lieu. Si ses places ont
     * été reprises entre-temps, la jauge est dépassée (survente) : l'équipe Sgilt est alertée. Le
     * passage PAID tire le code d'entrée de la commande (contenu de son QR).
     *
     * @param commandeId l'identifiant de la commande
     * @return la commande, si elle vient de passer payée ; vide si elle l'était déjà (événement
     *         rejoué), pour que l'appelant n'agisse qu'une fois (e-billet à l'acheteur)
     * @throws CommandeNotFoundException si la commande est introuvable
     */
    public Optional<Commande> markPaid(UUID commandeId) {
        Commande commande = findCommande(commandeId);
        if (commande.getStatus() == CommandeStatus.PAID) {
            log.info("Commande déjà payée, rien à faire — commandeId={}", commandeId);
            return Optional.empty();
        }
        int soldTickets = getSoldTickets(commande.getBilletterie()) + commande.getQuantity();
        commande.setStatus(CommandeStatus.PAID);
        commande.setCode(EntryCodeUtils.newCode());
        commandeRepository.save(commande);
        alertIfOversold(commande, soldTickets);
        return Optional.of(commande);
    }

    // Survente : les places payées, cette commande comprise, dépassent la jauge.
    private void alertIfOversold(Commande commande, int soldTickets) {
        int capacity = commande.getBilletterie().getCapacity();
        if (soldTickets <= capacity) {
            return;
        }
        log.warn("Survente : commande payée au-delà de la jauge — commandeId={}, places payées={}, jauge={}",
                commande.getId(), soldTickets, capacity);
        adminMailerService.sendOversoldAlert(commande, soldTickets);
    }

    private Commande findCommande(UUID commandeId) {
        return commandeRepository.findById(commandeId).orElseThrow(() -> new CommandeNotFoundException(commandeId));
    }

    // Crée le PaymentIntent de la commande, au montant de la commande ; renvoie son secret client.
    private String createPaymentIntent(Commande commande) {
        int amountCents = commande.getQuantity() * commande.getUnitPriceCents();
        return payClient.createPaymentIntent(new PaymentIntentRequest(commande.getId(), amountCents)).clientSecret();
    }

    // Places prises : celles des commandes payées et des commandes en attente pas encore expirées.
    private int takenTickets(Billetterie billetterie) {
        return getSoldTickets(billetterie) + getHeldTickets(billetterie);
    }

    /**
     * Retourne le nombre de places vendues d'une billetterie : celles de ses commandes payées.
     *
     * @param billetterie la billetterie
     * @return le nombre de places vendues
     */
    public int getSoldTickets(Billetterie billetterie) {
        return sumQuantities(commandeRepository.findByBilletterieIdAndStatus(billetterie.getId(), CommandeStatus.PAID));
    }

    /**
     * Retourne le nombre de places bloquées par un paiement en cours : celles des commandes en
     * attente pas encore expirées.
     *
     * @param billetterie la billetterie
     * @return le nombre de places bloquées
     */
    public int getHeldTickets(Billetterie billetterie) {
        return sumQuantities(commandeRepository.findByBilletterieIdAndStatusAndExpiresAtAfter(
                billetterie.getId(), CommandeStatus.PENDING, LocalDateTime.now()));
    }

    private static int sumQuantities(List<Commande> commandes) {
        return commandes.stream().mapToInt(Commande::getQuantity).sum();
    }
}
