package net.franzka.sgilt.core.evenement.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.CreateEventRequest;
import net.franzka.sgilt.core.evenement.dto.CreateEventResponse;
import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvenementServiceTest {

    private static final UUID USER_ID  = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock private EvenementRepository     evenementRepository;
    @Mock private ReservationService      reservationService;
    @Mock private PrestataireService      prestataireService;
    @Mock private EvenementMapper         evenementMapper;
    @Mock private JournalEvenementService journalEvenementService;
    @Mock private FileStorageService      fileStorageService;

    @InjectMocks
    private EvenementService evenementService;

    // ── Helpers partagés ────────────────────────────────────────────────────────

    private Evenement ownerEvent(UnaryOperator<Evenement.EvenementBuilder> configure) {
        Utilisateur owner = mock(Utilisateur.class);
        when(owner.getId()).thenReturn(USER_ID);
        return configure.apply(
                Evenement.builder().id(EVENT_ID).utilisateur(owner).date(LocalDate.now())
        ).build();
    }

    private void whenEventFound(Evenement event) {
        when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.empty());
        when(evenementMapper.toDetailDto(any(), any(), any())).thenReturn(mock(EventDetailDto.class));
    }

    @SuppressWarnings("unchecked")
    private List<ModificationChamp> captureModifications(Evenement event) {
        ArgumentCaptor<List<ModificationChamp>> captor =
                ArgumentCaptor.forClass((Class<List<ModificationChamp>>) (Class<?>) List.class);
        verify(journalEvenementService).save(eq(event), captor.capture());
        return captor.getValue();
    }

    private EventPatchDto emptyPatch() {
        return new EventPatchDto(null, null, null, null, null, null, null, null, null);
    }

    // ── GetUserEvents ────────────────────────────────────────────────────────────

    @Nested
    class GetUserEvents {

        @Test
        void givenUserWithOneEvent_whenGetUserEvents_thenReturnsMappedDto() {
            Evenement           event  = Evenement.builder().id(EVENT_ID).title("Mariage").date(LocalDate.now()).build();
            ReservationCounts   counts = new ReservationCounts(1, 0, 0);
            EvenementSummaryDto dto    = new EvenementSummaryDto(EVENT_ID, "Mariage", LocalDate.now(), null, null, null, 1, 0);

            when(evenementRepository.findByUtilisateurId(USER_ID)).thenReturn(List.of(event));
            when(reservationService.getCountsForEvenement(EVENT_ID)).thenReturn(counts);
            when(evenementMapper.toSummaryDto(event, counts)).thenReturn(dto);

            assertThat(evenementService.getUserEvents(USER_ID)).containsExactly(dto);
        }

        @Test
        void givenUserWithNoEvents_whenGetUserEvents_thenReturnsEmptyList() {
            when(evenementRepository.findByUtilisateurId(USER_ID)).thenReturn(List.of());

            assertThat(evenementService.getUserEvents(USER_ID)).isEmpty();
        }

        @Test
        void givenUserWithTwoEvents_whenGetUserEvents_thenEachEventQueriesItsOwnCounts() {
            UUID eventId2 = UUID.randomUUID();
            Evenement   event1  = Evenement.builder().id(EVENT_ID).title("E1").date(LocalDate.now()).build();
            Evenement   event2  = Evenement.builder().id(eventId2).title("E2").date(LocalDate.now()).build();
            ReservationCounts counts1 = new ReservationCounts(2, 0, 0);
            ReservationCounts counts2 = new ReservationCounts(0, 1, 0);
            EvenementSummaryDto dto1  = new EvenementSummaryDto(EVENT_ID, "E1", LocalDate.now(), null, null, null, 2, 0);
            EvenementSummaryDto dto2  = new EvenementSummaryDto(eventId2,  "E2", LocalDate.now(), null, null, null, 0, 1);

            when(evenementRepository.findByUtilisateurId(USER_ID)).thenReturn(List.of(event1, event2));
            when(reservationService.getCountsForEvenement(EVENT_ID)).thenReturn(counts1);
            when(reservationService.getCountsForEvenement(eventId2)).thenReturn(counts2);
            when(evenementMapper.toSummaryDto(event1, counts1)).thenReturn(dto1);
            when(evenementMapper.toSummaryDto(event2, counts2)).thenReturn(dto2);

            assertThat(evenementService.getUserEvents(USER_ID)).containsExactly(dto1, dto2);
        }
    }

    // ── GetEventDetail ────────────────────────────────────────────────────────────

    @Nested
    class GetEventDetail {

        @Test
        void givenNoJournalEntry_whenGetEventDetail_thenMapperCalledWithNullLastUpdateDate() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.empty());
            when(evenementMapper.toDetailDto(any(), any(), any())).thenReturn(mock(EventDetailDto.class));

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), any(), isNull());
        }

        @Test
        void givenJournalEntry_whenGetEventDetail_thenMapperCalledWithLastUpdateDate() {
            LocalDateTime lastUpdate = LocalDateTime.of(2026, 5, 12, 10, 0);
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.of(lastUpdate));
            when(evenementMapper.toDetailDto(any(), any(), any())).thenReturn(mock(EventDetailDto.class));

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), any(), eq(lastUpdate));
        }
    }

    // ── UpdateCover ───────────────────────────────────────────────────────────────

    @Nested
    class UpdateCover {

        private final MockMultipartFile file =
                new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        @Test
        void givenNoPreviousCover_whenUpdateCover_thenUploadsAndReturnsImagePath() throws IOException {
            String image = "new-uuid.jpg";
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(fileStorageService.upload(eq(file), any())).thenReturn(image);

            CoverUrlDto result = evenementService.updateCover(EVENT_ID, USER_ID, file);

            assertThat(result.imagePath()).isEqualTo(image);
            verify(fileStorageService, never()).delete(any());
            assertThat(event.getImagePath()).isEqualTo(image);
        }

        @Test
        void givenExistingCover_whenUpdateCover_thenDeletesOldAndUploadsNew() throws IOException {
            String oldImage = "old-uuid.jpg";
            String newImage = "new-uuid.jpg";
            Evenement event = ownerEvent(b -> b.imagePath(oldImage));
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(fileStorageService.upload(file, "uploads")).thenReturn(newImage);

            evenementService.updateCover(EVENT_ID, USER_ID, file);

            verify(fileStorageService).delete(oldImage);
            assertThat(event.getImagePath()).isEqualTo(newImage);
        }

        @Test
        void givenStorageFailure_whenUpdateCover_thenThrowsFileStorageException() throws IOException {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(fileStorageService.upload(eq(file), any())).thenThrow(new IOException("disk full"));

            assertThatThrownBy(() -> evenementService.updateCover(EVENT_ID, USER_ID, file))
                    .isInstanceOf(FileStorageException.class);
        }

        @Test
        void givenEventNotFound_whenUpdateCover_thenThrowsNotFoundException() {
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> evenementService.updateCover(EVENT_ID, USER_ID, file))
                    .isInstanceOf(EvenementNotFoundException.class);
        }

        @Test
        void givenWrongUser_whenUpdateCover_thenThrowsNotAllowedException() {
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> evenementService.updateCover(EVENT_ID, USER_ID, file))
                    .isInstanceOf(EvenementNotAllowedException.class);
        }
    }

    // ── VerifierAccesLectureJournal ───────────────────────────────────────────

    @Nested
    class VerifierAccesLectureJournal {

        @Test
        void givenOwner_whenVerifierAcces_thenNoException() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatNoException().isThrownBy(
                    () -> evenementService.verifierAccesLectureJournal(EVENT_ID, USER_ID)
            );
        }

        @Test
        void givenPrestataireWithReservation_whenVerifierAcces_thenNoException() {
            UUID prestataireUserId = UUID.randomUUID();
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.prestataireAReservationSurEvenement(EVENT_ID, prestataireUserId)).thenReturn(true);

            assertThatNoException().isThrownBy(
                    () -> evenementService.verifierAccesLectureJournal(EVENT_ID, prestataireUserId)
            );
        }

        @Test
        void givenUnauthorizedUser_whenVerifierAcces_thenThrowsNotAllowedException() {
            UUID randomUserId = UUID.randomUUID();
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.prestataireAReservationSurEvenement(EVENT_ID, randomUserId)).thenReturn(false);

            assertThatThrownBy(() -> evenementService.verifierAccesLectureJournal(EVENT_ID, randomUserId))
                    .isInstanceOf(EvenementNotAllowedException.class);
        }

        @Test
        void givenEventNotFound_whenVerifierAcces_thenThrowsNotFoundException() {
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> evenementService.verifierAccesLectureJournal(EVENT_ID, USER_ID))
                    .isInstanceOf(EvenementNotFoundException.class);
        }
    }

    // ── PatchEvent ────────────────────────────────────────────────────────────

    @Nested
    class PatchEvent {

        @Test
        void givenFieldChanged_whenPatchEvent_thenModificationLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventPatchDto patch = new EventPatchDto(null, "Lyon", null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("lieu", "Paris", "Lyon"));
        }

        @Test
        void givenFieldUnchanged_whenPatchEvent_thenFieldNotLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventPatchDto patch = new EventPatchDto(null, "Paris", null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event)).isEmpty();
        }

        @Test
        void givenNullPatchField_whenPatchEvent_thenFieldNotLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventPatchDto patch = new EventPatchDto(null, null, null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event)).isEmpty();
        }

        @Test
        void givenBlankLieu_whenPatchEvent_thenLoggedAsNull() {
            // blankToNull("") = null : un blank est loggué comme null
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventPatchDto patch = new EventPatchDto(null, "", null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("lieu", "Paris", null));
        }

        @Test
        void givenMultipleFieldsChanged_whenPatchEvent_thenAllLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris").ville("Paris"));
            EventPatchDto patch = new EventPatchDto(null, "Lyon", null, null, null, "Bordeaux", null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event)).containsExactlyInAnyOrder(
                    new ModificationChamp("lieu", "Paris", "Lyon"),
                    new ModificationChamp("ville", "Paris", "Bordeaux")
            );
        }

        @Test
        void givenTitleChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.title("Ancien titre"));
            EventPatchDto patch = new EventPatchDto("Nouveau titre", null, null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("titre", "Ancien titre", "Nouveau titre"));
            assertThat(event.getTitle()).isEqualTo("Nouveau titre");
        }

        @Test
        void givenEventTypeChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.eventType("Mariage"));
            EventPatchDto patch = new EventPatchDto(null, null, null, "Anniversaire", null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("eventType", "Mariage", "Anniversaire"));
            assertThat(event.getEventType()).isEqualTo("Anniversaire");
        }

        @Test
        void givenAmbianceChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.ambiance("Champetre"));
            EventPatchDto patch = new EventPatchDto(null, null, null, null, "Chic", null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("ambiance", "Champetre", "Chic"));
            assertThat(event.getAmbiance()).isEqualTo("Chic");
        }

        @Test
        void givenNbInvitesChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.nbInvites("50"));
            EventPatchDto patch = new EventPatchDto(null, null, null, null, null, null, "80", null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("nbInvites", "50", "80"));
            assertThat(event.getNbInvites()).isEqualTo("80");
        }

        @Test
        void givenDescriptionChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.description("Ancienne description"));
            EventPatchDto patch = new EventPatchDto(null, null, null, null, null, null, null, "Nouvelle description", null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("description", "Ancienne description", "Nouvelle description"));
            assertThat(event.getDescription()).isEqualTo("Nouvelle description");
        }

        @Test
        void givenMomentCleChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.momentCle("Vin d'honneur"));
            EventPatchDto patch = new EventPatchDto(null, null, null, null, null, null, null, null, "Ouverture du bal");

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("momentCle", "Vin d'honneur", "Ouverture du bal"));
            assertThat(event.getMomentCle()).isEqualTo("Ouverture du bal");
        }

        @Test
        void givenSharedNoteChangedWithBlank_whenPatchEvent_thenLoggedAsBlankNotNull() {
            // sharedNote n'est pas soumis à blankToNull : "" reste "" dans le log
            Evenement event = ownerEvent(b -> b.notePartagee(null));
            EventPatchDto patch = new EventPatchDto(null, null, "", null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("notePartagee", null, ""));
        }

        @Test
        void givenNothingChanged_whenPatchEvent_thenSaveCalledWithEmptyList() {
            Evenement event = ownerEvent(b -> b.lieu("Paris").ville("Lyon"));
            EventPatchDto patch = new EventPatchDto(null, "Paris", null, null, null, "Lyon", null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event)).isEmpty();
        }

        @Test
        void givenEventNotFound_whenPatchEvent_thenThrowsEvenementNotFoundException() {
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

            var patch = emptyPatch();
            assertThatThrownBy(() -> evenementService.patchEvent(EVENT_ID, USER_ID, patch))
                    .isInstanceOf(EvenementNotFoundException.class);
        }

        @Test
        void givenWrongUser_whenPatchEvent_thenThrowsEvenementNotAllowedException() {
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            var patch = emptyPatch();
            assertThatThrownBy(() -> evenementService.patchEvent(EVENT_ID, USER_ID, patch))
                    .isInstanceOf(EvenementNotAllowedException.class);
        }
    }

    // ── CreateEvent ───────────────────────────────────────────────────────────

    @Nested
    class CreateEvent {

        @Test
        void givenValidRequest_whenCreateEvent_thenSavesEventAndCreatesReservation() {
            Utilisateur utilisateur = mock(Utilisateur.class);
            UUID prestataireId = UUID.randomUUID();
            LocalDate date = LocalDate.of(2027, 6, 15);
            CreateEventRequest request = new CreateEventRequest(
                    prestataireId, "Mariage", "Champetre", "Vin d'honneur", "Description",
                    date, "Lyon", "80", "Domaine des fleurs", "Bonjour");
            Prestataire prestataire = mock(Prestataire.class);
            when(evenementRepository.save(any())).thenAnswer(invocation -> {
                Evenement e = invocation.getArgument(0);
                e.setId(EVENT_ID);
                return e;
            });
            when(prestataireService.getById(prestataireId)).thenReturn(prestataire);

            CreateEventResponse response = evenementService.createEvent(utilisateur, request);

            assertThat(response.eventId()).isEqualTo(EVENT_ID);
            ArgumentCaptor<Evenement> captor = ArgumentCaptor.forClass(Evenement.class);
            verify(evenementRepository).save(captor.capture());
            Evenement saved = captor.getValue();
            assertThat(saved.getUtilisateur()).isEqualTo(utilisateur);
            assertThat(saved.getLieu()).isEqualTo("Domaine des fleurs");
            assertThat(saved.getVille()).isEqualTo("Lyon");
            assertThat(saved.getNbInvites()).isEqualTo("80");
            verify(reservationService).create(saved, prestataire, utilisateur, date, "Bonjour");
        }

        @Test
        void givenNoDate_whenCreateEvent_thenTitleDefaultsToGenericName() {
            Utilisateur utilisateur = mock(Utilisateur.class);
            UUID prestataireId = UUID.randomUUID();
            CreateEventRequest request = new CreateEventRequest(
                    prestataireId, null, null, null, null, null, null, null, null, null);
            when(evenementRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(prestataireService.getById(prestataireId)).thenReturn(mock(Prestataire.class));

            evenementService.createEvent(utilisateur, request);

            ArgumentCaptor<Evenement> captor = ArgumentCaptor.forClass(Evenement.class);
            verify(evenementRepository).save(captor.capture());
            assertThat(captor.getValue().getTitle()).isEqualTo("Mon événement");
        }
    }

    // ── AddReservation ────────────────────────────────────────────────────────

    @Nested
    class AddReservation {

        @Test
        void givenOwner_whenAddReservation_thenCreatesReservationOnEvent() {
            UUID prestataireId = UUID.randomUUID();
            Utilisateur utilisateur = mock(Utilisateur.class);
            when(utilisateur.getId()).thenReturn(USER_ID);
            Evenement event = ownerEvent(b -> b);
            Prestataire prestataire = mock(Prestataire.class);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(prestataireService.getById(prestataireId)).thenReturn(prestataire);

            evenementService.addReservation(EVENT_ID, utilisateur, prestataireId, "Bonjour");

            verify(reservationService).create(event, prestataire, utilisateur, event.getDate(), "Bonjour");
        }

        @Test
        void givenWrongUser_whenAddReservation_thenThrowsNotAllowedException() {
            UUID prestataireId = UUID.randomUUID();
            Utilisateur utilisateur = mock(Utilisateur.class);
            when(utilisateur.getId()).thenReturn(UUID.randomUUID());
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> evenementService.addReservation(EVENT_ID, utilisateur, prestataireId, "Bonjour"))
                    .isInstanceOf(EvenementNotAllowedException.class);
            verify(reservationService, never()).create(any(), any(), any(), any(), any());
        }
    }

    // ── CreateFromFormData ────────────────────────────────────────────────────

    @Nested
    class CreateFromFormData {

        @Test
        void givenFormData_whenCreateFromFormData_thenSavesActiveEvent() {
            Utilisateur utilisateur = mock(Utilisateur.class);
            LocalDate date = LocalDate.of(2027, 3, 1);
            InitOnboardingRequest formData = new InitOnboardingRequest(
                    "Jean", "Dupont", "jean@sgilt.fr", UUID.randomUUID(), "Mariage", "Champetre",
                    "Vin d'honneur", "Description", date, "Lyon", "80", "Domaine des fleurs",
                    "0102030405", "Bonjour");
            Evenement saved = Evenement.builder().id(EVENT_ID).build();
            when(evenementRepository.save(any())).thenReturn(saved);

            Evenement result = evenementService.createFromFormData(utilisateur, formData);

            assertThat(result).isEqualTo(saved);
            ArgumentCaptor<Evenement> captor = ArgumentCaptor.forClass(Evenement.class);
            verify(evenementRepository).save(captor.capture());
            Evenement toSave = captor.getValue();
            assertThat(toSave.getUtilisateur()).isEqualTo(utilisateur);
            assertThat(toSave.getStatus()).isEqualTo(net.franzka.sgilt.core.evenement.domain.EvenementStatus.ACTIVE);
            assertThat(toSave.getVille()).isEqualTo("Lyon");
        }
    }

    // ── GetEventCounts ────────────────────────────────────────────────────────

    @Nested
    class GetEventCounts {

        @Test
        void givenNoActiveReservation_whenGetEventCounts_thenMoodIsDefaut() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.getStatusCountsByEvenement(EVENT_ID)).thenReturn(Map.of());

            EventCountsDto result = evenementService.getEventCounts(EVENT_ID, USER_ID);

            assertThat(result).isEqualTo(new EventCountsDto("defaut", 0, 0, 0, 0, 0, 0));
        }

        @Test
        void givenMoreConfirmedThanOthers_whenGetEventCounts_thenMoodIsConfirmee() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.getStatusCountsByEvenement(EVENT_ID)).thenReturn(Map.of(
                    ReservationStatus.CONFIRMED, 3,
                    ReservationStatus.IN_DISCUSSION, 1
            ));

            EventCountsDto result = evenementService.getEventCounts(EVENT_ID, USER_ID);

            assertThat(result.mood()).isEqualTo("confirmee");
            assertThat(result.confirmedCount()).isEqualTo(3);
            assertThat(result.inDiscussionCount()).isEqualTo(1);
        }

        @Test
        void givenInDiscussionButNotEnoughConfirmed_whenGetEventCounts_thenMoodIsEnDiscussion() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.getStatusCountsByEvenement(EVENT_ID)).thenReturn(Map.of(
                    ReservationStatus.CONFIRMED, 1,
                    ReservationStatus.IN_DISCUSSION, 2
            ));

            assertThat(evenementService.getEventCounts(EVENT_ID, USER_ID).mood()).isEqualTo("en_discussion");
        }

        @Test
        void givenOnlyNewReservations_whenGetEventCounts_thenMoodIsNouvelle() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.getStatusCountsByEvenement(EVENT_ID)).thenReturn(Map.of(
                    ReservationStatus.NEW, 2
            ));

            assertThat(evenementService.getEventCounts(EVENT_ID, USER_ID).mood()).isEqualTo("nouvelle");
        }

        @Test
        void givenRefusedAndCanceledCounts_whenGetEventCounts_thenSumsBothVariants() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(reservationService.getStatusCountsByEvenement(EVENT_ID)).thenReturn(Map.of(
                    ReservationStatus.REFUSED_PRE_CONTACT, 1,
                    ReservationStatus.REFUSED_POST_CONTACT, 2,
                    ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT, 1,
                    ReservationStatus.CANCELED_BY_CLIENT_POST_CONTACT, 1,
                    ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION, 1,
                    ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION, 1,
                    ReservationStatus.DONE, 5
            ));

            EventCountsDto result = evenementService.getEventCounts(EVENT_ID, USER_ID);

            assertThat(result.refuseeCount()).isEqualTo(3);
            assertThat(result.annuleeCount()).isEqualTo(4);
            assertThat(result.realiseeCount()).isEqualTo(5);
        }

        @Test
        void givenWrongUser_whenGetEventCounts_thenThrowsNotAllowedException() {
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> evenementService.getEventCounts(EVENT_ID, USER_ID))
                    .isInstanceOf(EvenementNotAllowedException.class);
        }
    }

    // ── SelectCover ───────────────────────────────────────────────────────────

    @Nested
    class SelectCover {

        @Test
        void givenNoPreviousCover_whenSelectCover_thenSetsImagePathWithoutDeleting() throws IOException {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            CoverUrlDto result = evenementService.selectCover(EVENT_ID, USER_ID, "bank/plage.jpg");

            assertThat(result.imagePath()).isEqualTo("bank/plage.jpg");
            assertThat(event.getImagePath()).isEqualTo("bank/plage.jpg");
            verify(fileStorageService, never()).delete(any());
        }

        @Test
        void givenProtectedPreviousCover_whenSelectCover_thenDoesNotDeleteBankImage() throws IOException {
            Evenement event = ownerEvent(b -> b.imagePath("bank/ancienne.jpg"));
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            evenementService.selectCover(EVENT_ID, USER_ID, "bank/plage.jpg");

            verify(fileStorageService, never()).delete(any());
        }

        @Test
        void givenPreviousUploadedCover_whenSelectCover_thenDeletesOldUpload() throws IOException {
            Evenement event = ownerEvent(b -> b.imagePath("uploads/ancienne.jpg"));
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            evenementService.selectCover(EVENT_ID, USER_ID, "bank/plage.jpg");

            verify(fileStorageService).delete("uploads/ancienne.jpg");
        }

        @Test
        void givenDeletionFailure_whenSelectCover_thenThrowsFileStorageException() throws IOException {
            Evenement event = ownerEvent(b -> b.imagePath("uploads/ancienne.jpg"));
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            doThrow(new IOException("erreur disque")).when(fileStorageService).delete("uploads/ancienne.jpg");

            assertThatThrownBy(() -> evenementService.selectCover(EVENT_ID, USER_ID, "bank/plage.jpg"))
                    .isInstanceOf(FileStorageException.class);
        }

        @Test
        void givenWrongUser_whenSelectCover_thenThrowsNotAllowedException() {
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> evenementService.selectCover(EVENT_ID, USER_ID, "bank/plage.jpg"))
                    .isInstanceOf(EvenementNotAllowedException.class);
        }
    }

    // ── VerifyEventOwnership ──────────────────────────────────────────────────

    @Nested
    class VerifyEventOwnership {

        @Test
        void givenOwner_whenVerifyEventOwnership_thenNoException() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatNoException().isThrownBy(() -> evenementService.verifyEventOwnership(EVENT_ID, USER_ID));
        }

        @Test
        void givenWrongUser_whenVerifyEventOwnership_thenThrowsNotAllowedException() {
            Utilisateur otherOwner = mock(Utilisateur.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            Evenement event = Evenement.builder().id(EVENT_ID).utilisateur(otherOwner).date(LocalDate.now()).build();
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> evenementService.verifyEventOwnership(EVENT_ID, USER_ID))
                    .isInstanceOf(EvenementNotAllowedException.class);
        }
    }

    // ── Countdown (via GetEventDetail, computeCountdown est privé) ───────────

    @Nested
    class Countdown {

        @Test
        void givenNoDate_whenGetEventDetail_thenCountdownIsSerein() {
            Evenement event = ownerEvent(b -> b.date(null));
            whenEventFound(event);

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), eq("serein"), any());
        }

        @Test
        void givenPastDate_whenGetEventDetail_thenCountdownIsPast() {
            Evenement event = ownerEvent(b -> b.date(LocalDate.now().minusDays(1)));
            whenEventFound(event);

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), eq("past"), any());
        }

        @Test
        void givenDateWithin30Days_whenGetEventDetail_thenCountdownIsImminent() {
            Evenement event = ownerEvent(b -> b.date(LocalDate.now().plusDays(10)));
            whenEventFound(event);

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), eq("imminent"), any());
        }

        @Test
        void givenDateWithin90Days_whenGetEventDetail_thenCountdownIsProche() {
            Evenement event = ownerEvent(b -> b.date(LocalDate.now().plusDays(60)));
            whenEventFound(event);

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), eq("proche"), any());
        }

        @Test
        void givenDateBeyond90Days_whenGetEventDetail_thenCountdownIsSerein() {
            Evenement event = ownerEvent(b -> b.date(LocalDate.now().plusDays(200)));
            whenEventFound(event);

            evenementService.getEventDetail(EVENT_ID, USER_ID);

            verify(evenementMapper).toDetailDto(eq(event), eq("serein"), any());
        }
    }
}
