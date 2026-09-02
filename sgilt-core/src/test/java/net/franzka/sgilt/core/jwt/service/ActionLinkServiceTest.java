package net.franzka.sgilt.core.jwt.service;

import net.franzka.sgilt.core.jwt.domain.ActionToken;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionLinkServiceTest {

    private static final String FRONTEND_URL = "https://sgilt.fr";

    @Mock
    private ActionTokenService actionTokenService;

    @InjectMocks
    private ActionLinkService actionLinkService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(actionLinkService, "frontendUrl", FRONTEND_URL);
    }

    // -------------------------------------------------------------------------
    // createLink
    // -------------------------------------------------------------------------

    @Nested
    class CreateLink {

        @Test
        void givenTypeAndPayload_whenCreateLink_thenCreatesTokenAndBuildsFrontUrl() {
            Map<String, String> payload = Map.of("email", "pro@sgilt.fr");
            ActionToken token = ActionToken.builder().type(ActionType.PRESTATAIRE_ONBOARDING).build();
            when(actionTokenService.createToken(ActionType.PRESTATAIRE_ONBOARDING, payload))
                    .thenReturn(new ActionTokenService.TokenCreationResult(token, "hmac-token-value"));

            String link = actionLinkService.createLink(ActionType.PRESTATAIRE_ONBOARDING, payload);

            assertThat(link).isEqualTo("https://sgilt.fr/onboarding/verify?token=hmac-token-value");
        }

        @Test
        void givenTokenWithSpecialCharacters_whenCreateLink_thenUrlEncodesToken() {
            when(actionTokenService.createToken(ActionType.PRESTATAIRE_ONBOARDING, "payload"))
                    .thenReturn(new ActionTokenService.TokenCreationResult(null, "a+b/c=d"));

            String link = actionLinkService.createLink(ActionType.PRESTATAIRE_ONBOARDING, "payload");

            assertThat(link).isEqualTo("https://sgilt.fr/onboarding/verify?token=a%2Bb%2Fc%3Dd");
        }
    }

    // -------------------------------------------------------------------------
    // rebuildLink
    // -------------------------------------------------------------------------

    @Nested
    class RebuildLink {

        @Test
        void givenExistingToken_whenRebuildLink_thenRenewsExpirationAndBuildsFrontUrl() {
            ActionToken token = ActionToken.builder().type(ActionType.PRESTATAIRE_ONBOARDING).build();
            when(actionTokenService.renewExpiration(token)).thenReturn("hmac-token-value");

            String link = actionLinkService.rebuildLink(token);

            assertThat(link).isEqualTo("https://sgilt.fr/onboarding/verify?token=hmac-token-value");
        }
    }
}
