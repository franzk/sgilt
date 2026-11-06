package net.franzka.sgilt.core.admin.mailer;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service d'envoi des emails d'information à l'équipe Sgilt (incidents techniques, plus tard
 * nouvelles réservations…), à l'adresse {@code sgilt.admin.email} propre à chaque environnement.
 * Délègue l'envoi effectif à {@link MailerClient}, qui compose le HTML
 * à partir du {@link MailType} et des variables fournies.
 */
@Service
@Slf4j
public class AdminMailerService {

    private final MailerClient mailerClient;
    private final String adminEmail;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param mailerClient le client vers sgilt-mailer
     * @param adminEmail   l'adresse de l'équipe Sgilt
     */
    public AdminMailerService(
            MailerClient mailerClient,
            @Value("${sgilt.admin.email}") String adminEmail) {
        this.mailerClient = mailerClient;
        this.adminEmail = adminEmail;
    }

    /**
     * Alerte l'équipe Sgilt d'une survente : la commande vient d'être payée alors que la jauge de
     * sa billetterie est dépassée. Le paiement est acquis ; à l'équipe de décider (rembourser depuis
     * le dashboard Stripe, ou accepter la survente).
     *
     * @param commande    la commande payée qui fait dépasser la jauge
     * @param soldTickets le nombre de billets payés de la billetterie, cette commande comprise
     */
    public void sendOversoldAlert(Commande commande, int soldTickets) {
        Billetterie billetterie = commande.getBilletterie();
        Evenement evenement = billetterie.getEvenement();

        log.info("sendOversoldAlert → {} (commandeId={})", adminEmail, commande.getId());
        mailerClient.sendMail(new MailRequest(
                adminEmail,
                MailType.ADMIN_OVERSOLD_ALERT_EMAIL,
                Map.of(
                        "eventTitle", evenement.getTitle(),
                        "eventDate", evenement.getDate().toString(),
                        "commandeId", commande.getId().toString(),
                        "buyerName", commande.getBuyerFirstName() + " " + commande.getBuyerLastName(),
                        "buyerEmail", commande.getBuyerEmail(),
                        "quantity", commande.getQuantity(),
                        "soldTickets", soldTickets,
                        "capacity", billetterie.getCapacity()),
                null));
    }
}
