package net.franzka.sgilt.core.evenement.billetterie.commande.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.mailer.CommandeMailerService;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import net.franzka.sgilt.core.pay.PaymentSucceededEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static net.franzka.sgilt.core.pay.PaymentEventsConfig.PAYMENT_SUCCEEDED_QUEUE;

/**
 * Consomme les paiements réussis publiés par sgilt-pay : marque les commandes payées et envoie
 * l'e-billet à l'acheteur. Orchestre la commande et la page de son événement (slug, infos), comme
 * CommandeController pour l'e-billet : CommandeService ne peut pas appeler PageEvenementService
 * (BilletterieService dépend déjà de CommandeService). Comme côté sgilt-mailer : le retry configuré
 * sur le listener s'applique à toute exception avant dead-letter.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSucceededListener {

    private final CommandeService       commandeService;
    private final PageEvenementService  pageEvenementService;
    private final CommandeMailerService commandeMailerService;

    /**
     * Marque payée la commande d'un paiement réussi et, si elle vient de passer payée, envoie son
     * e-billet à l'acheteur (un paiement rejoué n'envoie rien). {@code @Transactional} porté ici
     * faute de controller appelant, comme {@code ReservationReminderService.sendContactPendingReminders}.
     *
     * @param event le paiement réussi
     */
    @Transactional
    @RabbitListener(queues = PAYMENT_SUCCEEDED_QUEUE)
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("onPaymentSucceeded : commandeId={} paymentIntentId={}", event.commandeId(), event.paymentIntentId());
        commandeService.markPaid(event.commandeId()).ifPresent(this::sendEBilletEmail);
    }

    // Sans page (impossible en pratique : on commande depuis /e/{slug}), pas de lien à envoyer ; la
    // commande reste payée, l'erreur est tracée.
    private void sendEBilletEmail(Commande commande) {
        Evenement evenement = commande.getBilletterie().getEvenement();
        pageEvenementService.getSlug(evenement).ifPresentOrElse(
                slug -> commandeMailerService.sendEBilletEmail(
                        commande, slug, pageEvenementService.getEvenementInfos(evenement)),
                () -> log.error("Événement sans page, e-billet non envoyé — commandeId={}", commande.getId()));
    }
}
