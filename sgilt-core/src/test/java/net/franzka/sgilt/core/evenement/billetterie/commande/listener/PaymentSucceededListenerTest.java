package net.franzka.sgilt.core.evenement.billetterie.commande.listener;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.mailer.CommandeMailerService;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import net.franzka.sgilt.core.pay.PaymentSucceededEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentSucceededListenerTest {

    @Mock
    private CommandeService commandeService;

    @Mock
    private PageEvenementService pageEvenementService;

    @Mock
    private CommandeMailerService commandeMailerService;

    @InjectMocks
    private PaymentSucceededListener listener;

    // -------------------------------------------------------------------------
    // onPaymentSucceeded
    // -------------------------------------------------------------------------

    @Nested
    class OnPaymentSucceeded {

        private final UUID commandeId = UUID.randomUUID();
        private final PaymentSucceededEvent event = new PaymentSucceededEvent(commandeId, "pi_123");
        private final Evenement evenement = Evenement.builder().build();
        private final Commande commande = Commande.builder()
                .id(commandeId).billetterie(Billetterie.builder().evenement(evenement).build()).build();

        @Test
        void givenCommandeJustPaid_whenOnPaymentSucceeded_thenSendsEBilletWithSlugAndInfos() {
            EvenementInfosDto evenementInfos = mock(EvenementInfosDto.class);
            when(commandeService.markPaid(commandeId)).thenReturn(Optional.of(commande));
            when(pageEvenementService.getSlug(evenement)).thenReturn(Optional.of("jeudis-taennel"));
            when(pageEvenementService.getEvenementInfos(evenement)).thenReturn(evenementInfos);

            listener.onPaymentSucceeded(event);

            verify(commandeMailerService).sendEBilletEmail(commande, "jeudis-taennel", evenementInfos);
        }

        @Test
        void givenCommandeAlreadyPaid_whenOnPaymentSucceeded_thenSendsNothing() {
            when(commandeService.markPaid(commandeId)).thenReturn(Optional.empty());

            listener.onPaymentSucceeded(event);

            verifyNoInteractions(pageEvenementService, commandeMailerService);
        }

        @Test
        void givenEventWithoutPage_whenOnPaymentSucceeded_thenSendsNothing() {
            when(commandeService.markPaid(commandeId)).thenReturn(Optional.of(commande));
            when(pageEvenementService.getSlug(evenement)).thenReturn(Optional.empty());

            listener.onPaymentSucceeded(event);

            verify(commandeMailerService, never()).sendEBilletEmail(any(), any(), any());
        }
    }
}
