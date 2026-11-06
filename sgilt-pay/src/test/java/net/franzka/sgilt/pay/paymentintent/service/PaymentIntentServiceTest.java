package net.franzka.sgilt.pay.paymentintent.service;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.AllowedPaymentMethodType;
import net.franzka.sgilt.pay.paymentintent.dto.CreatePaymentIntentRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentIntentServiceTest {

    private static final UUID COMMANDE_ID = UUID.randomUUID();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StripeClient stripeClient;

    @InjectMocks
    private PaymentIntentService paymentIntentService;

    // -------------------------------------------------------------------------
    // createPaymentIntent
    // -------------------------------------------------------------------------

    @Nested
    class CreatePaymentIntent {

        @Test
        void givenCommande_whenCreatePaymentIntent_thenCreatesItInEurosForCardsAndReturnsClientSecret()
                throws StripeException {
            PaymentIntent paymentIntent = mock(PaymentIntent.class);
            when(paymentIntent.getClientSecret()).thenReturn("pi_123_secret_456");
            when(stripeClient.v1().paymentIntents().create(any(PaymentIntentCreateParams.class), any(RequestOptions.class)))
                    .thenReturn(paymentIntent);
            ArgumentCaptor<PaymentIntentCreateParams> params = ArgumentCaptor.forClass(PaymentIntentCreateParams.class);
            ArgumentCaptor<RequestOptions> options = ArgumentCaptor.forClass(RequestOptions.class);

            assertThat(paymentIntentService.createPaymentIntent(new CreatePaymentIntentRequest(COMMANDE_ID, 4000)).clientSecret())
                    .isEqualTo("pi_123_secret_456");

            verify(stripeClient.v1().paymentIntents()).create(params.capture(), options.capture());
            assertThat(params.getValue().getAmount()).isEqualTo(4000L);
            assertThat(params.getValue().getCurrency()).isEqualTo("eur");
            assertThat(params.getValue().getAllowedPaymentMethodTypes())
                    .containsExactly(AllowedPaymentMethodType.CARD);
            assertThat(params.getValue().getMetadata()).containsEntry("commandeId", COMMANDE_ID.toString());
            assertThat(options.getValue().getIdempotencyKey()).isEqualTo(COMMANDE_ID.toString());
        }
    }
}
