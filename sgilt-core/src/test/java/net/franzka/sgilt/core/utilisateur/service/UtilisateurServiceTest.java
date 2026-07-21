package net.franzka.sgilt.core.utilisateur.service;

import net.bytebuddy.utility.RandomString;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurAlreadyExistException;
import net.franzka.sgilt.core.utilisateur.repository.UtilisateurRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.exception.UtilisateurNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    private static final String FIRST_NAME = RandomString.make(64);
    private static final String LAST_NAME  = RandomString.make(64);
    private static final String EMAIL      = RandomString.make(64) + "@example.com";
    private static final String TELEPHONE  = RandomString.make(10);

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    // -------------------------------------------------------------------------
    // createUtilisateur
    // -------------------------------------------------------------------------

    @Nested
    class CreateUtilisateur {

        @Test
        void givenExistingEmail_whenCreateUtilisateur_thenThrowsUtilisateurAlreadyExistException() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatExceptionOfType(UtilisateurAlreadyExistException.class)
                    .isThrownBy(() -> utilisateurService.createUtilisateur(FIRST_NAME, LAST_NAME, EMAIL, TELEPHONE));
        }

        @Test
        void givenExistingEmail_whenCreateUtilisateur_thenDoesNotSave() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatExceptionOfType(UtilisateurAlreadyExistException.class)
                    .isThrownBy(() -> utilisateurService.createUtilisateur(FIRST_NAME, LAST_NAME, EMAIL, TELEPHONE));

            verify(utilisateurRepository, never()).save(any());
        }

        @Test
        void givenNewEmail_whenCreateUtilisateur_thenSavesUtilisateurWithCorrectFields() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(utilisateurRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            utilisateurService.createUtilisateur(FIRST_NAME, LAST_NAME, EMAIL, TELEPHONE);

            ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
            verify(utilisateurRepository).save(captor.capture());

            Utilisateur saved = captor.getValue();
            assertThat(saved.getFirstName()).isEqualTo(FIRST_NAME);
            assertThat(saved.getLastName()).isEqualTo(LAST_NAME);
            assertThat(saved.getEmail()).isEqualTo(EMAIL);
            assertThat(saved.getPhone()).isEqualTo(TELEPHONE);
        }

        @Test
        void givenNewEmail_whenCreateUtilisateur_thenReturnsPersistedUtilisateur() {
            Utilisateur persisted = Utilisateur.builder().email(EMAIL).build();
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(utilisateurRepository.save(any())).thenReturn(persisted);

            Utilisateur result = utilisateurService.createUtilisateur(FIRST_NAME, LAST_NAME, EMAIL, TELEPHONE);

            assertThat(result).isSameAs(persisted);
        }
    }

    // -------------------------------------------------------------------------
    // existsByEmail
    // -------------------------------------------------------------------------

    @Nested
    class ExistsByEmail {

        @Test
        void givenExistingEmail_whenExistsByEmail_thenReturnsTrue() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThat(utilisateurService.existsByEmail(EMAIL)).isTrue();
        }

        @Test
        void givenUnknownEmail_whenExistsByEmail_thenReturnsFalse() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(false);

            assertThat(utilisateurService.existsByEmail(EMAIL)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // getByEmail
    // -------------------------------------------------------------------------

    @Nested
    class GetByEmail {

        @Test
        void givenExistingEmail_whenGetByEmail_thenReturnsUtilisateur() {
            Utilisateur user = Utilisateur.builder().email(EMAIL).build();
            when(utilisateurRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));

            assertThat(utilisateurService.getByEmail(EMAIL)).isSameAs(user);
        }

        @Test
        void givenUnknownEmail_whenGetByEmail_thenThrowsUtilisateurNotFoundException() {
            when(utilisateurRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

            assertThatExceptionOfType(UtilisateurNotFoundException.class)
                    .isThrownBy(() -> utilisateurService.getByEmail(EMAIL));
        }
    }

    // -------------------------------------------------------------------------
    // getProfile
    // -------------------------------------------------------------------------

    @Nested
    class GetProfile {

        @Test
        void givenExistingEmail_whenGetProfile_thenReturnsDtoWithCorrectFields() {
            Utilisateur user = Utilisateur.builder()
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .email(EMAIL)
                    .avatarUrl(null)
                    .build();
            when(utilisateurRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));

            UtilisateurProfileDto result = utilisateurService.getProfile(EMAIL);

            assertThat(result.firstName()).isEqualTo(FIRST_NAME);
            assertThat(result.lastName()).isEqualTo(LAST_NAME);
            assertThat(result.email()).isEqualTo(EMAIL);
            assertThat(result.avatarUrl()).isNull();
        }

        @Test
        void givenUnknownEmail_whenGetProfile_thenThrowsUtilisateurNotFoundException() {
            when(utilisateurRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());

            assertThatExceptionOfType(UtilisateurNotFoundException.class)
                    .isThrownBy(() -> utilisateurService.getProfile(EMAIL));
        }
    }
}
