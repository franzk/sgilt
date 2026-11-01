package net.franzka.sgilt.core.admin.mailer;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminMailerServiceTest {

    private static final String ADMIN_EMAIL = "admin@sgilt.fr";

    @Mock
    private MailerClient mailerClient;

    private AdminMailerService adminMailerService;

    @BeforeEach
    void setUp() {
        adminMailerService = new AdminMailerService(mailerClient, ADMIN_EMAIL);
    }

    // -------------------------------------------------------------------------
    // sendOversoldAlert
    // -------------------------------------------------------------------------

    @Nested
    class SendOversoldAlert {

        @Test
        void givenOversoldCommande_whenSendOversoldAlert_thenSendsAlertToAdmin() {
            UUID commandeId = UUID.randomUUID();
            Evenement evenement = Evenement.builder()
                    .title("Les Jeudis du Taennel").date(LocalDate.of(2026, 12, 3)).build();
            Billetterie billetterie = Billetterie.builder().evenement(evenement).capacity(100).build();
            Commande commande = Commande.builder()
                    .id(commandeId).billetterie(billetterie).quantity(3)
                    .buyerFirstName("Jeanne").buyerLastName("Martin").buyerEmail("jeanne@example.com")
                    .build();

            adminMailerService.sendOversoldAlert(commande, 102);

            verify(mailerClient).sendMail(new MailRequest(
                    ADMIN_EMAIL, MailType.ADMIN_OVERSOLD_ALERT_EMAIL,
                    Map.of(
                            "eventTitle", "Les Jeudis du Taennel",
                            "eventDate", "2026-12-03",
                            "commandeId", commandeId.toString(),
                            "buyerName", "Jeanne Martin",
                            "buyerEmail", "jeanne@example.com",
                            "quantity", 3,
                            "soldTickets", 102,
                            "capacity", 100),
                    null));
        }
    }
}
