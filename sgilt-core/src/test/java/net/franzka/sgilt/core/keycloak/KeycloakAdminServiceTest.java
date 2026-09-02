package net.franzka.sgilt.core.keycloak;

import net.franzka.sgilt.core.config.KeycloakProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakAdminServiceTest {

    private static final KeycloakProperties PROPERTIES = new KeycloakProperties(
            "https://kc.sgilt.fr", "sgilt", "admin-client", "admin-secret", "front-client", "magic-secret-key");

    @Mock
    private KeycloakTokenClient keycloakTokenClient;

    @Mock
    private KeycloakAdminClient keycloakAdminClient;

    private KeycloakAdminService keycloakAdminService;

    @BeforeEach
    void setUp() {
        keycloakAdminService = new KeycloakAdminService(keycloakTokenClient, keycloakAdminClient, PROPERTIES);
        ReflectionTestUtils.setField(keycloakAdminService, "frontendUrl", "https://sgilt.fr");
    }

    private void stubAdminToken() {
        when(keycloakTokenClient.fetchToken(eq("sgilt"), any())).thenReturn(
                new KeycloakTokenResponse("admin-access-token", "admin-refresh-token"));
    }

    private ResponseEntity<Void> createdResponseWithLocation(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "https://kc.sgilt.fr/admin/realms/sgilt/users/" + userId);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // -------------------------------------------------------------------------
    // createClientUser
    // -------------------------------------------------------------------------

    @Nested
    class CreateClientUser {

        @Test
        void givenSuccess_whenCreateClientUser_thenCreatesUserWithPasswordCredentialAndAssignsUserRole() {
            stubAdminToken();
            String userId = "kc-user-id";
            when(keycloakAdminClient.createUser(eq("sgilt"), eq("Bearer admin-access-token"), any()))
                    .thenReturn(createdResponseWithLocation(userId));
            KeycloakRoleRepresentation role = new KeycloakRoleRepresentation("role-id", "USER");
            when(keycloakAdminClient.getRole("sgilt", "Bearer admin-access-token", "USER")).thenReturn(role);

            keycloakAdminService.createClientUser("client@sgilt.fr", "Jean", "Dupont", "s3cret!");

            ArgumentCaptor<KeycloakUserRequest> captor = ArgumentCaptor.forClass(KeycloakUserRequest.class);
            verify(keycloakAdminClient).createUser(eq("sgilt"), eq("Bearer admin-access-token"), captor.capture());
            assertThat(captor.getValue()).isEqualTo(new KeycloakUserRequest(
                    "client@sgilt.fr", "client@sgilt.fr", "Jean", "Dupont", true, true,
                    List.of(new KeycloakCredential("password", "s3cret!", false))));
            verify(keycloakAdminClient).assignRoles("sgilt", "Bearer admin-access-token", userId, List.of(role));
        }

        @Test
        void givenEmailAlreadyExists_whenCreateClientUser_thenThrowsUserAlreadyExists() {
            stubAdminToken();
            when(keycloakAdminClient.createUser(eq("sgilt"), anyString(), any()))
                    .thenThrow(conflict());

            assertThatThrownBy(() -> keycloakAdminService.createClientUser("client@sgilt.fr", "Jean", "Dupont", "s3cret!"))
                    .isInstanceOf(KeycloakUserAlreadyExistsException.class);
        }

        @Test
        void givenMissingLocationHeader_whenCreateClientUser_thenThrowsKeycloakException() {
            stubAdminToken();
            when(keycloakAdminClient.createUser(eq("sgilt"), anyString(), any()))
                    .thenReturn(new ResponseEntity<>(new HttpHeaders(), HttpStatus.CREATED));

            assertThatThrownBy(() -> keycloakAdminService.createClientUser("client@sgilt.fr", "Jean", "Dupont", "s3cret!"))
                    .isInstanceOf(KeycloakException.class);
        }

        @Test
        void givenTechnicalFailure_whenCreateClientUser_thenThrowsKeycloakException() {
            stubAdminToken();
            when(keycloakAdminClient.createUser(eq("sgilt"), anyString(), any()))
                    .thenThrow(new ResourceAccessException("connexion refusée"));

            assertThatThrownBy(() -> keycloakAdminService.createClientUser("client@sgilt.fr", "Jean", "Dupont", "s3cret!"))
                    .isInstanceOf(KeycloakException.class);
        }
    }

    // -------------------------------------------------------------------------
    // createProUserWithoutPassword
    // -------------------------------------------------------------------------

    @Nested
    class CreateProUserWithoutPassword {

        @Test
        void givenSuccess_whenCreateProUserWithoutPassword_thenCreatesUserWithoutCredentialAndAssignsProRole() {
            stubAdminToken();
            String userId = "kc-pro-id";
            when(keycloakAdminClient.createUser(eq("sgilt"), anyString(), any()))
                    .thenReturn(createdResponseWithLocation(userId));
            KeycloakRoleRepresentation role = new KeycloakRoleRepresentation("role-id", "PRO");
            when(keycloakAdminClient.getRole("sgilt", "Bearer admin-access-token", "PRO")).thenReturn(role);

            String result = keycloakAdminService.createProUserWithoutPassword("pro@sgilt.fr", "Jean", "Dupont");

            assertThat(result).isEqualTo(userId);
            ArgumentCaptor<KeycloakUserRequest> captor = ArgumentCaptor.forClass(KeycloakUserRequest.class);
            verify(keycloakAdminClient).createUser(eq("sgilt"), anyString(), captor.capture());
            assertThat(captor.getValue().credentials()).isEmpty();
            verify(keycloakAdminClient).assignRoles("sgilt", "Bearer admin-access-token", userId, List.of(role));
        }
    }

    // -------------------------------------------------------------------------
    // deleteUser
    // -------------------------------------------------------------------------

    @Nested
    class DeleteUser {

        @Test
        void givenUserId_whenDeleteUser_thenDeletesViaAdminClient() {
            stubAdminToken();

            keycloakAdminService.deleteUser("kc-user-id");

            verify(keycloakAdminClient).deleteUser("sgilt", "Bearer admin-access-token", "kc-user-id");
        }

        @Test
        void givenTechnicalFailure_whenDeleteUser_thenThrowsKeycloakException() {
            stubAdminToken();
            org.mockito.Mockito.doThrow(new ResourceAccessException("connexion refusée"))
                    .when(keycloakAdminClient).deleteUser(eq("sgilt"), anyString(), eq("kc-user-id"));

            assertThatThrownBy(() -> keycloakAdminService.deleteUser("kc-user-id"))
                    .isInstanceOf(KeycloakException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getUserIdByEmail
    // -------------------------------------------------------------------------

    @Nested
    class GetUserIdByEmail {

        @Test
        void givenExistingUser_whenGetUserIdByEmail_thenReturnsFirstMatchId() {
            stubAdminToken();
            when(keycloakAdminClient.getUsersByEmail("sgilt", "Bearer admin-access-token", "pro@sgilt.fr", true))
                    .thenReturn(List.of(new KeycloakUserRepresentation("kc-user-id")));

            assertThat(keycloakAdminService.getUserIdByEmail("pro@sgilt.fr")).isEqualTo("kc-user-id");
        }

        @Test
        void givenNoMatch_whenGetUserIdByEmail_thenThrowsKeycloakException() {
            stubAdminToken();
            when(keycloakAdminClient.getUsersByEmail("sgilt", "Bearer admin-access-token", "unknown@sgilt.fr", true))
                    .thenReturn(List.of());

            assertThatThrownBy(() -> keycloakAdminService.getUserIdByEmail("unknown@sgilt.fr"))
                    .isInstanceOf(KeycloakException.class);
        }

        @Test
        void givenTechnicalFailure_whenGetUserIdByEmail_thenThrowsKeycloakException() {
            stubAdminToken();
            when(keycloakAdminClient.getUsersByEmail(eq("sgilt"), anyString(), eq("pro@sgilt.fr"), eq(true)))
                    .thenThrow(new ResourceAccessException("connexion refusée"));

            assertThatThrownBy(() -> keycloakAdminService.getUserIdByEmail("pro@sgilt.fr"))
                    .isInstanceOf(KeycloakException.class);
        }
    }

    // -------------------------------------------------------------------------
    // resetPassword
    // -------------------------------------------------------------------------

    @Nested
    class ResetPassword {

        @Test
        void givenUserIdAndPassword_whenResetPassword_thenDelegatesToAdminClient() {
            stubAdminToken();

            keycloakAdminService.resetPassword("kc-user-id", "n3wP4ss!");

            verify(keycloakAdminClient).resetPassword("sgilt", "Bearer admin-access-token", "kc-user-id",
                    new KeycloakCredential("password", "n3wP4ss!", false));
        }

        @Test
        void givenTechnicalFailure_whenResetPassword_thenThrowsKeycloakException() {
            stubAdminToken();
            org.mockito.Mockito.doThrow(new ResourceAccessException("connexion refusée"))
                    .when(keycloakAdminClient).resetPassword(eq("sgilt"), anyString(), eq("kc-user-id"), any());

            assertThatThrownBy(() -> keycloakAdminService.resetPassword("kc-user-id", "n3wP4ss!"))
                    .isInstanceOf(KeycloakException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getMagicLoginUrl
    // -------------------------------------------------------------------------

    @Nested
    class GetMagicLoginUrl {

        @Test
        void givenEmail_whenGetMagicLoginUrl_thenBuildsExpectedUrlStructure() {
            String url = keycloakAdminService.getMagicLoginUrl("pro@sgilt.fr", "/pro/onboarding");

            assertThat(url).startsWith("https://kc.sgilt.fr/realms/sgilt/protocol/openid-connect/auth");
            assertThat(url).contains("client_id=front-client");
            assertThat(url).contains("redirect_uri=https%3A%2F%2Fsgilt.fr%2Fpro%2Fonboarding");
            assertThat(url).contains("response_type=code");
            assertThat(url).contains("scope=openid");
            assertThat(url).contains("magic_token=");
        }

        @Test
        void givenTwoCalls_whenGetMagicLoginUrl_thenTokensDiffer() throws InterruptedException {
            String url1 = keycloakAdminService.getMagicLoginUrl("pro@sgilt.fr", "/pro/onboarding");
            Thread.sleep(1100); // le token embarque un iat en secondes
            String url2 = keycloakAdminService.getMagicLoginUrl("pro@sgilt.fr", "/pro/onboarding");

            String token1 = url1.substring(url1.indexOf("magic_token=") + "magic_token=".length());
            String token2 = url2.substring(url2.indexOf("magic_token=") + "magic_token=".length());
            assertThat(token1).isNotEqualTo(token2);
        }
    }

    private HttpClientErrorException.Conflict conflict() {
        return (HttpClientErrorException.Conflict) HttpClientErrorException.create(
                HttpStatus.CONFLICT, "Conflict", new HttpHeaders(), new byte[0], null);
    }
}
