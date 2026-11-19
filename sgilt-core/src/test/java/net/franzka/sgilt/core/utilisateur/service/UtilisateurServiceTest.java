package net.franzka.sgilt.core.utilisateur.service;

import net.bytebuddy.utility.RandomString;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.domain.UtilisateurStatus;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurEditDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurUpdateDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    // -------------------------------------------------------------------------
    // acceptCgu
    // -------------------------------------------------------------------------

    @Nested
    class AcceptCgu {

        @Test
        void givenUtilisateur_whenAcceptCgu_thenSetsVersionAndTimestampAndSaves() {
            Utilisateur utilisateur = Utilisateur.builder().email(EMAIL).build();

            utilisateurService.acceptCgu(utilisateur);

            assertThat(utilisateur.getCguVersionAccepted()).isEqualTo("2026-06");
            assertThat(utilisateur.getCguAcceptedAt()).isCloseTo(LocalDateTime.now(), within3Seconds());
            verify(utilisateurRepository).save(utilisateur);
        }
    }

    // -------------------------------------------------------------------------
    // getEditProfile
    // -------------------------------------------------------------------------

    @Nested
    class GetEditProfile {

        @Test
        void givenUtilisateur_whenGetEditProfile_thenReturnsEditableFields() {
            Utilisateur utilisateur = Utilisateur.builder()
                    .firstName(FIRST_NAME).lastName(LAST_NAME).phone(TELEPHONE).email(EMAIL).build();

            UtilisateurEditDto dto = utilisateurService.getEditProfile(utilisateur);

            assertThat(dto).isEqualTo(new UtilisateurEditDto(FIRST_NAME, LAST_NAME, TELEPHONE, EMAIL));
        }
    }

    // -------------------------------------------------------------------------
    // updateProfile
    // -------------------------------------------------------------------------

    @Nested
    class UpdateProfile {

        @Test
        void givenAllFieldsSet_whenUpdateProfile_thenUpdatesAndSaves() {
            Utilisateur utilisateur = Utilisateur.builder()
                    .firstName("Ancien").lastName("Nom").phone("0000000000").email(EMAIL).build();
            UtilisateurUpdateDto dto = new UtilisateurUpdateDto(FIRST_NAME, LAST_NAME, TELEPHONE);

            utilisateurService.updateProfile(utilisateur, dto);

            assertThat(utilisateur.getFirstName()).isEqualTo(FIRST_NAME);
            assertThat(utilisateur.getLastName()).isEqualTo(LAST_NAME);
            assertThat(utilisateur.getPhone()).isEqualTo(TELEPHONE);
            verify(utilisateurRepository).save(utilisateur);
        }

        @Test
        void givenAllFieldsNull_whenUpdateProfile_thenLeavesFieldsUnchangedButSaves() {
            Utilisateur utilisateur = Utilisateur.builder()
                    .firstName("Ancien").lastName("Nom").phone("0000000000").email(EMAIL).build();
            UtilisateurUpdateDto dto = new UtilisateurUpdateDto(null, null, null);

            utilisateurService.updateProfile(utilisateur, dto);

            assertThat(utilisateur.getFirstName()).isEqualTo("Ancien");
            assertThat(utilisateur.getLastName()).isEqualTo("Nom");
            assertThat(utilisateur.getPhone()).isEqualTo("0000000000");
            verify(utilisateurRepository).save(utilisateur);
        }
    }

    private org.assertj.core.data.TemporalUnitOffset within3Seconds() {
        return org.assertj.core.api.Assertions.within(3, java.time.temporal.ChronoUnit.SECONDS);
    }

    // -------------------------------------------------------------------------
    // createInvitedUtilisateur
    // -------------------------------------------------------------------------

    @Nested
    class CreateInvitedUtilisateur {

        @Test
        void givenExistingEmail_whenCreateInvitedUtilisateur_thenThrowsWithoutSaving() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatExceptionOfType(UtilisateurAlreadyExistException.class)
                    .isThrownBy(() -> utilisateurService.createInvitedUtilisateur(FIRST_NAME, LAST_NAME, EMAIL));
            verify(utilisateurRepository, never()).save(any());
        }

        @Test
        void givenNewEmail_whenCreateInvitedUtilisateur_thenSavesUtilisateurInvited() {
            when(utilisateurRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(utilisateurRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Utilisateur saved = utilisateurService.createInvitedUtilisateur(FIRST_NAME, LAST_NAME, EMAIL);

            assertThat(saved.getFirstName()).isEqualTo(FIRST_NAME);
            assertThat(saved.getLastName()).isEqualTo(LAST_NAME);
            assertThat(saved.getEmail()).isEqualTo(EMAIL);
            assertThat(saved.getPhone()).isNull();
            assertThat(saved.getStatus()).isEqualTo(UtilisateurStatus.INVITED);
        }
    }

    // -------------------------------------------------------------------------
    // listInvitedUtilisateurs
    // -------------------------------------------------------------------------

    @Nested
    class ListInvitedUtilisateurs {

        @Test
        void givenInvitedUtilisateurs_whenListInvitedUtilisateurs_thenReturnsThem() {
            List<Utilisateur> invited = List.of(Utilisateur.builder().email(EMAIL).build());
            when(utilisateurRepository.findByStatus(UtilisateurStatus.INVITED)).thenReturn(invited);

            assertThat(utilisateurService.listInvitedUtilisateurs()).isEqualTo(invited);
        }
    }

    // -------------------------------------------------------------------------
    // getInvitedUtilisateur
    // -------------------------------------------------------------------------

    @Nested
    class GetInvitedUtilisateur {

        @Test
        void givenInvitedUtilisateur_whenGetInvitedUtilisateur_thenReturnsIt() {
            UUID id = UUID.randomUUID();
            Utilisateur invited = Utilisateur.builder().id(id).status(UtilisateurStatus.INVITED).build();
            when(utilisateurRepository.findById(id)).thenReturn(Optional.of(invited));

            assertThat(utilisateurService.getInvitedUtilisateur(id)).isEqualTo(invited);
        }

        @Test
        void givenActivatedUtilisateur_whenGetInvitedUtilisateur_thenThrowsNotFound() {
            UUID id = UUID.randomUUID();
            Utilisateur active = Utilisateur.builder().id(id).status(UtilisateurStatus.ACTIVE).build();
            when(utilisateurRepository.findById(id)).thenReturn(Optional.of(active));

            assertThatThrownBy(() -> utilisateurService.getInvitedUtilisateur(id))
                    .isInstanceOf(UtilisateurNotFoundException.class);
        }

        @Test
        void givenUnknownUtilisateur_whenGetInvitedUtilisateur_thenThrowsNotFound() {
            UUID id = UUID.randomUUID();
            when(utilisateurRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> utilisateurService.getInvitedUtilisateur(id))
                    .isInstanceOf(UtilisateurNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // activateInvitedUtilisateur
    // -------------------------------------------------------------------------

    @Nested
    class ActivateInvitedUtilisateur {

        @Test
        void givenInvitedUtilisateur_whenActivateInvitedUtilisateur_thenSavesItActive() {
            Utilisateur invited = Utilisateur.builder().email(EMAIL).status(UtilisateurStatus.INVITED).build();
            when(utilisateurRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(invited));
            when(utilisateurRepository.save(invited)).thenReturn(invited);

            Utilisateur result = utilisateurService.activateInvitedUtilisateur(EMAIL);

            assertThat(result.getStatus()).isEqualTo(UtilisateurStatus.ACTIVE);
            verify(utilisateurRepository).save(invited);
        }

        @Test
        void givenAlreadyActiveUtilisateur_whenActivateInvitedUtilisateur_thenThrowsNotFound() {
            Utilisateur active = Utilisateur.builder().email(EMAIL).status(UtilisateurStatus.ACTIVE).build();
            when(utilisateurRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(active));

            assertThatThrownBy(() -> utilisateurService.activateInvitedUtilisateur(EMAIL))
                    .isInstanceOf(UtilisateurNotFoundException.class);
            verify(utilisateurRepository, never()).save(any());
        }
    }
}
