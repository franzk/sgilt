package net.franzka.sgilt.core.evenement.billetterie.commande.mailer;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommandeMailerServiceTest {

    private static final String FRONTEND_URL = "https://sgilt.fr";
    private static final UUID COMMANDE_ID = UUID.fromString("7c9e6679-7425-40de-944b-e07fc1f90ae7");
    private static final String E_BILLET_URL = "https://sgilt.fr/e/jeudis-taennel/e-billet?commande=" + COMMANDE_ID;

    @Mock
    private MailerClient mailerClient;

    private CommandeMailerService commandeMailerService;

    @BeforeEach
    void setUp() {
        commandeMailerService = new CommandeMailerService(mailerClient, FRONTEND_URL);
    }

    private Commande commande(int quantity) {
        Evenement evenement = Evenement.builder().title("Les Jeudis du Taennel").build();
        return Commande.builder()
                .id(COMMANDE_ID).billetterie(Billetterie.builder().evenement(evenement).build())
                .quantity(quantity).buyerFirstName("Julie").buyerEmail("julie@mail.fr").build();
    }

    private EvenementInfosDto evenementInfos(String venue, String city) {
        return new EvenementInfosDto(null, null, LocalDate.of(2026, 12, 3),
                LocalDateTime.of(2026, 12, 3, 19, 0), LocalDateTime.of(2026, 12, 4, 2, 0), venue, city, List.of());
    }

    // -------------------------------------------------------------------------
    // sendEBilletEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendEBilletEmail {

        @Test
        void givenPaidCommande_whenSendEBilletEmail_thenSendsLinkToEBilletWithEventInfos() {
            commandeMailerService.sendEBilletEmail(commande(4), "jeudis-taennel", evenementInfos("Le Taennel", "Scherwiller (67)"));

            verify(mailerClient).sendMail(new MailRequest(
                    "julie@mail.fr", MailType.COMMANDE_E_BILLET_EMAIL,
                    Map.of(
                            "firstName", "Julie",
                            "eventTitle", "Les Jeudis du Taennel",
                            "validFor", "4 personnes",
                            "eventDate", "Jeudi 3 décembre 2026 · 19h – 2h",
                            "venue", "Le Taennel",
                            "city", "Scherwiller (67)",
                            "eBilletUrl", E_BILLET_URL),
                    null));
        }

        @Test
        void givenSinglePlaceAndNoVenue_whenSendEBilletEmail_thenSendsSingularAndEmptyVenue() {
            commandeMailerService.sendEBilletEmail(commande(1), "jeudis-taennel", evenementInfos(null, null));

            verify(mailerClient).sendMail(new MailRequest(
                    "julie@mail.fr", MailType.COMMANDE_E_BILLET_EMAIL,
                    Map.of(
                            "firstName", "Julie",
                            "eventTitle", "Les Jeudis du Taennel",
                            "validFor", "1 personne",
                            "eventDate", "Jeudi 3 décembre 2026 · 19h – 2h",
                            "venue", "",
                            "city", "",
                            "eBilletUrl", E_BILLET_URL),
                    null));
        }

        @Test
        void givenMailerFailure_whenSendEBilletEmail_thenDoesNotThrow() {
            doThrow(new AmqpException("broker down")).when(mailerClient).sendMail(any());

            assertThatCode(() -> commandeMailerService.sendEBilletEmail(
                    commande(4), "jeudis-taennel", evenementInfos("Le Taennel", "Scherwiller (67)")))
                    .doesNotThrowAnyException();
        }
    }
}
