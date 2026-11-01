package net.franzka.sgilt.pay.webhook.service;

import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import net.franzka.sgilt.pay.config.StripeProperties;
import net.franzka.sgilt.pay.webhook.event.PaymentEventPublisher;
import net.franzka.sgilt.pay.webhook.event.PaymentSucceededEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StripeWebhookServiceTest {

    private static final String PAYLOAD = "{\"id\":\"evt_123\"}";
    private static final String SIGNATURE = "t=1,v1=abc";
    private static final String WEBHOOK_SECRET = "whsec_test";

    @Mock
    private StripeClient stripeClient;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    private StripeWebhookService stripeWebhookService;

    @BeforeEach
    void setUp() {
        stripeWebhookService = new StripeWebhookService(
                stripeClient, new StripeProperties("sk_test", WEBHOOK_SECRET), paymentEventPublisher);
    }

    // -------------------------------------------------------------------------
    // handleEvent
    // -------------------------------------------------------------------------

    @Nested
    class HandleEvent {

        @Test
        void givenInvalidSignature_whenHandleEvent_thenThrowsSignatureVerificationException()
                throws SignatureVerificationException {
            when(stripeClient.constructEvent(PAYLOAD, SIGNATURE, WEBHOOK_SECRET))
                    .thenThrow(new SignatureVerificationException("bad signature", SIGNATURE));

            assertThatThrownBy(() -> stripeWebhookService.handleEvent(PAYLOAD, SIGNATURE))
                    .isInstanceOf(SignatureVerificationException.class);
        }

        @Test
        void givenOtherEventType_whenHandleEvent_thenIgnoresItWithoutReadingItsObject()
                throws SignatureVerificationException {
            Event event = mock(Event.class);
            when(event.getType()).thenReturn("payment_intent.created");
            when(stripeClient.constructEvent(PAYLOAD, SIGNATURE, WEBHOOK_SECRET)).thenReturn(event);

            stripeWebhookService.handleEvent(PAYLOAD, SIGNATURE);

            verify(event, never()).getDataObjectDeserializer();
            verify(paymentEventPublisher, never()).publishPaymentSucceeded(any());
        }

        @Test
        void givenSucceededPaymentIntentOfCommande_whenHandleEvent_thenPublishesPaymentSucceeded()
                throws SignatureVerificationException {
            UUID commandeId = UUID.randomUUID();
            PaymentIntent paymentIntent = mock(PaymentIntent.class);
            when(paymentIntent.getId()).thenReturn("pi_123");
            when(paymentIntent.getMetadata()).thenReturn(Map.of("commandeId", commandeId.toString()));
            Event event = succeededEvent(paymentIntent);
            when(stripeClient.constructEvent(PAYLOAD, SIGNATURE, WEBHOOK_SECRET)).thenReturn(event);

            stripeWebhookService.handleEvent(PAYLOAD, SIGNATURE);

            verify(paymentEventPublisher).publishPaymentSucceeded(new PaymentSucceededEvent(commandeId, "pi_123"));
        }

        @Test
        void givenSucceededPaymentIntentWithoutCommande_whenHandleEvent_thenIgnoresItWithoutError()
                throws SignatureVerificationException {
            PaymentIntent paymentIntent = mock(PaymentIntent.class);
            when(paymentIntent.getMetadata()).thenReturn(Map.of());
            Event event = succeededEvent(paymentIntent);
            when(stripeClient.constructEvent(PAYLOAD, SIGNATURE, WEBHOOK_SECRET)).thenReturn(event);

            assertThatCode(() -> stripeWebhookService.handleEvent(PAYLOAD, SIGNATURE)).doesNotThrowAnyException();
            verify(paymentEventPublisher, never()).publishPaymentSucceeded(any());
        }

        private Event succeededEvent(PaymentIntent paymentIntent) {
            EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
            when(deserializer.getObject()).thenReturn(Optional.of(paymentIntent));
            Event event = mock(Event.class);
            when(event.getType()).thenReturn("payment_intent.succeeded");
            when(event.getDataObjectDeserializer()).thenReturn(deserializer);
            return event;
        }
    }
}
