package net.franzka.sgilt.core.evenement.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.image.ImageStorageException;
import net.franzka.sgilt.core.image.ImageStorageService;
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
    @Mock private EvenementMapper         evenementMapper;
    @Mock private JournalEvenementService journalEvenementService;
    @Mock private ImageStorageService     imageStorageService;

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
            when(evenementMapper.toSummaryDto(eq(event), eq(counts))).thenReturn(dto);

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
            when(evenementMapper.toSummaryDto(eq(event1), eq(counts1))).thenReturn(dto1);
            when(evenementMapper.toSummaryDto(eq(event2), eq(counts2))).thenReturn(dto2);

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
            when(imageStorageService.upload(file)).thenReturn(image);

            CoverUrlDto result = evenementService.updateCover(EVENT_ID, USER_ID, file);

            assertThat(result.imagePath()).isEqualTo(image);
            verify(imageStorageService, never()).delete(any());
            assertThat(event.getImagePath()).isEqualTo(image);
        }

        @Test
        void givenExistingCover_whenUpdateCover_thenDeletesOldAndUploadsNew() throws IOException {
            String oldImage = "old-uuid.jpg";
            String newImage = "new-uuid.jpg";
            Evenement event = ownerEvent(b -> b.imagePath(oldImage));
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(imageStorageService.upload(file)).thenReturn(newImage);

            evenementService.updateCover(EVENT_ID, USER_ID, file);

            verify(imageStorageService).delete(oldImage);
            assertThat(event.getImagePath()).isEqualTo(newImage);
        }

        @Test
        void givenStorageFailure_whenUpdateCover_thenThrowsImageStorageException() throws IOException {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(imageStorageService.upload(file)).thenThrow(new IOException("disk full"));

            assertThatThrownBy(() -> evenementService.updateCover(EVENT_ID, USER_ID, file))
                    .isInstanceOf(ImageStorageException.class);
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
}
