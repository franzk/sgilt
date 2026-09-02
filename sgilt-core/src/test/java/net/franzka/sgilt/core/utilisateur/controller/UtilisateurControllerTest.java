package net.franzka.sgilt.core.utilisateur.controller;

import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurEditDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurUpdateDto;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilisateurControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UtilisateurController controller;

    private final Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).email("client@sgilt.fr").build();

    // -------------------------------------------------------------------------
    // getMe
    // -------------------------------------------------------------------------

    @Nested
    class GetMe {

        @Test
        void givenCurrentUserEmail_whenGetMe_thenReturnsProfile() {
            when(currentUserService.getEmail()).thenReturn("client@sgilt.fr");
            UtilisateurProfileDto dto = mock(UtilisateurProfileDto.class);
            when(utilisateurService.getProfile("client@sgilt.fr")).thenReturn(dto);

            assertThat(controller.getMe().getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getEditProfile
    // -------------------------------------------------------------------------

    @Nested
    class GetEditProfile {

        @Test
        void givenCurrentUser_whenGetEditProfile_thenReturnsEditableFields() {
            when(currentUserService.get()).thenReturn(utilisateur);
            UtilisateurEditDto dto = mock(UtilisateurEditDto.class);
            when(utilisateurService.getEditProfile(utilisateur)).thenReturn(dto);

            assertThat(controller.getEditProfile().getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // updateMe
    // -------------------------------------------------------------------------

    @Nested
    class UpdateMe {

        @Test
        void givenUpdateDto_whenUpdateMe_thenDelegatesAndReturnsNoContent() {
            when(currentUserService.get()).thenReturn(utilisateur);
            UtilisateurUpdateDto dto = new UtilisateurUpdateDto("Jean", "Dupont", "0102030405");

            ResponseEntity<Void> response = controller.updateMe(dto);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(utilisateurService).updateProfile(utilisateur, dto);
        }
    }
}
