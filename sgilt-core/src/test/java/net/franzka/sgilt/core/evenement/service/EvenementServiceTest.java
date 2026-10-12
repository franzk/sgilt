package net.franzka.sgilt.core.evenement.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementRubrique;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.CreateEventInConnectedFlowDemandeRequest;
import net.franzka.sgilt.core.evenement.dto.CreateEventResponse;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.EventDto;
import net.franzka.sgilt.core.evenement.dto.EventMetaDto;
import net.franzka.sgilt.core.evenement.dto.EventMetaPatchDto;
import net.franzka.sgilt.core.evenement.dto.EventRubriqueDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.evenement.exception.EvenementNotAllowedException;
import net.franzka.sgilt.core.evenement.exception.EvenementNotFoundException;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.template.service.TemplateService;
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
    @Mock private PrestataireService      prestataireService;
    @Mock private EvenementMapper         evenementMapper;
    @Mock private JournalEvenementService journalEvenementService;
    @Mock private FileStorageService      fileStorageService;
    @Mock private TemplateService         templateService;

    @InjectMocks
    private EvenementService evenementService;

    // ── Helpers partagés ────────────────────────────────────────────────────────

    private Evenement ownerEvent(UnaryOperator<Evenement.EvenementBuilder> configure) {
        Utilisateur owner = mock(Utilisateur.class);
        when(owner.getId()).thenReturn(USER_ID);
        return configure.apply(
                Evenement.builder().id(EVENT_ID).utilisateur(owner).date(LocalDate.now()).rubriques(List.of())
        ).build();
    }

    private void whenEventFound(Evenement event) {
        when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.empty());
        when(evenementMapper.toMetaDto(any(), any())).thenReturn(mock(EventMetaDto.class));
    }

    @SuppressWarnings("unchecked")
    private List<ModificationChamp> captureModifications(Evenement event) {
        ArgumentCaptor<List<ModificationChamp>> captor =
                ArgumentCaptor.forClass((Class<List<ModificationChamp>>) (Class<?>) List.class);
        verify(journalEvenementService).save(eq(event), captor.capture());
        return captor.getValue();
    }

    private EventMetaPatchDto emptyPatch() {
        return new EventMetaPatchDto(null, null, null, null, null, null, null, null, null);
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

    // ── GetEvent ────────────────────────────────────────────────────────────

    @Nested
    class GetEvent {

        @Test
        void givenNoJournalEntry_whenGetEvent_thenMapperCalledWithNullLastUpdateDate() {
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.empty());
            when(evenementMapper.toMetaDto(any(), any())).thenReturn(mock(EventMetaDto.class));

            evenementService.getEvent(EVENT_ID, USER_ID);

            verify(evenementMapper).toMetaDto(eq(event), isNull());
        }

        @Test
        void givenJournalEntry_whenGetEvent_thenMapperCalledWithLastUpdateDate() {
            LocalDateTime lastUpdate = LocalDateTime.of(2026, 5, 12, 10, 0);
            Evenement event = ownerEvent(b -> b);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.of(lastUpdate));
            when(evenementMapper.toMetaDto(any(), any())).thenReturn(mock(EventMetaDto.class));

            evenementService.getEvent(EVENT_ID, USER_ID);

            verify(evenementMapper).toMetaDto(eq(event), eq(lastUpdate));
        }

        @Test
        void givenReservations_whenGetEvent_thenReturnsTheMetaAndEachRubriqueWithItsReservationsInTheEventOrder() {
            Evenement event = ownerEvent(b -> b.eventType("mariage").rubriques(List.of(
                    new EvenementRubrique("lieu"), new EvenementRubrique("musique-animation"),
                    new EvenementRubrique("decoration"))));
            EventMetaDto meta = mock(EventMetaDto.class);
            ReservationSummaryDto dj = summary("dj", "musique-animation");
            ReservationSummaryDto chanteur = summary("pop-rock", "musique-animation");
            ReservationSummaryDto salle = summary("salle", "lieu");
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.empty());
            when(evenementMapper.toMetaDto(any(), any())).thenReturn(meta);
            when(reservationService.getReservationSummaries(EVENT_ID)).thenReturn(List.of(dj, salle, chanteur));

            EventDto detail = evenementService.getEvent(EVENT_ID, USER_ID);

            assertThat(detail.meta()).isSameAs(meta);
            assertThat(detail.rubriques()).containsExactly(
                    new EventRubriqueDto("lieu", List.of(salle)),
                    new EventRubriqueDto("musique-animation", List.of(dj, chanteur)),
                    new EventRubriqueDto("decoration", List.of()));
        }

        // Résumé d'une réservation dont le prestataire a la sous-catégorie donnée, rangée dans la rubrique donnée.
        private ReservationSummaryDto summary(String subcatKey, String rubriqueKey) {
            return new ReservationSummaryDto(UUID.randomUUID(), UUID.randomUUID(), "Presta " + subcatKey,
                    null, "categorie", subcatKey, rubriqueKey, "nouvelle", 0);
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
            EventMetaPatchDto patch = new EventMetaPatchDto(null, "Lyon", null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("lieu", "Paris", "Lyon"));
        }

        @Test
        void givenFieldUnchanged_whenPatchEvent_thenFieldNotLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, "Paris", null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event)).isEmpty();
        }

        @Test
        void givenNullPatchField_whenPatchEvent_thenFieldNotLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event)).isEmpty();
        }

        @Test
        void givenBlankLieu_whenPatchEvent_thenLoggedAsNull() {
            // blankToNull("") = null : un blank est loggué comme null
            Evenement event = ownerEvent(b -> b.lieu("Paris"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, "", null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("lieu", "Paris", null));
        }

        @Test
        void givenMultipleFieldsChanged_whenPatchEvent_thenAllLogged() {
            Evenement event = ownerEvent(b -> b.lieu("Paris").ville("Paris"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, "Lyon", null, null, null, "Bordeaux", null, null, null);

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
            EventMetaPatchDto patch = new EventMetaPatchDto("Nouveau titre", null, null, null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("titre", "Ancien titre", "Nouveau titre"));
            assertThat(event.getTitle()).isEqualTo("Nouveau titre");
        }

        @Test
        void givenEventTypeChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.eventType("Mariage"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, null, "Anniversaire", null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("eventType", "Mariage", "Anniversaire"));
            assertThat(event.getEventType()).isEqualTo("Anniversaire");
        }

        @Test
        void givenAmbianceChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.ambiance("Champetre"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, null, null, "Chic", null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("ambiance", "Champetre", "Chic"));
            assertThat(event.getAmbiance()).isEqualTo("Chic");
        }

        @Test
        void givenNbInvitesChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.nbInvites("50"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, null, null, null, null, "80", null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("nbInvites", "50", "80"));
            assertThat(event.getNbInvites()).isEqualTo("80");
        }

        @Test
        void givenDescriptionChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.description("Ancienne description"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, null, null, null, null, null, "Nouvelle description", null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("description", "Ancienne description", "Nouvelle description"));
            assertThat(event.getDescription()).isEqualTo("Nouvelle description");
        }

        @Test
        void givenMomentCleChanged_whenPatchEvent_thenModificationLoggedAndEntityUpdated() {
            Evenement event = ownerEvent(b -> b.momentCle("Vin d'honneur"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, null, null, null, null, null, null, "Ouverture du bal");

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
            EventMetaPatchDto patch = new EventMetaPatchDto(null, null, "", null, null, null, null, null, null);

            whenEventFound(event);

            evenementService.patchEvent(EVENT_ID, USER_ID, patch);

            assertThat(captureModifications(event))
                    .containsExactly(new ModificationChamp("notePartagee", null, ""));
        }

        @Test
        void givenNothingChanged_whenPatchEvent_thenSaveCalledWithEmptyList() {
            Evenement event = ownerEvent(b -> b.lieu("Paris").ville("Lyon"));
            EventMetaPatchDto patch = new EventMetaPatchDto(null, "Paris", null, null, null, "Lyon", null, null, null);

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

    // ── AddReservation ────────────────────────────────────────────────────────

    @Nested
    class AddReservation {

        private final UUID prestataireId = UUID.randomUUID();
        private final Prestataire dj = Prestataire.builder().id(prestataireId).subcatKey("dj").build();

        // Le propriétaire ajoute le DJ à son événement de mariage, dont les rubriques sont données.
        private Utilisateur givenOwnerAddingTheDjTo(Evenement event) {
            Utilisateur utilisateur = mock(Utilisateur.class);
            when(utilisateur.getId()).thenReturn(USER_ID);
            when(evenementRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
            when(prestataireService.getById(prestataireId)).thenReturn(dj);
            when(templateService.rubriqueOf("mariage", "dj")).thenReturn("musique-animation");
            return utilisateur;
        }

        @Test
        void givenRubriqueAlreadyInTheEvent_whenAddReservation_thenCreatesTheReservationInItWithoutTouchingTheEvent() {
            Evenement event = ownerEvent(b -> b.eventType("mariage").rubriques(List.of(
                    new EvenementRubrique("lieu"), new EvenementRubrique("musique-animation"))));
            Utilisateur utilisateur = givenOwnerAddingTheDjTo(event);

            evenementService.addReservation(EVENT_ID, utilisateur, prestataireId, "Bonjour");

            verify(reservationService).create(event, dj, utilisateur, event.getDate(), "Bonjour", "musique-animation");
            verify(evenementRepository, never()).save(any());
        }

        @Test
        void givenRubriqueMissingFromTheEvent_whenAddReservation_thenInsertsItWhereTheTemplateSaysAndSavesTheEvent() {
            EvenementRubrique lieu = new EvenementRubrique("lieu");
            EvenementRubrique decoration = new EvenementRubrique("decoration");
            Evenement event = ownerEvent(b -> b.eventType("mariage").rubriques(List.of(lieu, decoration)));
            Utilisateur utilisateur = givenOwnerAddingTheDjTo(event);
            when(templateService.insertionPosition("mariage", List.of("lieu", "decoration"), "musique-animation"))
                    .thenReturn(1);

            evenementService.addReservation(EVENT_ID, utilisateur, prestataireId, "Bonjour");

            assertThat(event.getRubriques()).containsExactly(lieu, new EvenementRubrique("musique-animation"), decoration);
            verify(evenementRepository).save(event);
            verify(reservationService).create(event, dj, utilisateur, event.getDate(), "Bonjour", "musique-animation");
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
            verify(reservationService, never()).create(any(), any(), any(), any(), any(), any());
        }
    }

    // ── CreateEventFromDemande ────────────────────────────────────────────────

    @Nested
    class CreateEventFromDemande {

        @Test
        void givenSingleDemande_whenCreateEventFromDemande_thenCreatesTheEventWithTheTemplateRubriques() {
            UUID prestataireId = UUID.randomUUID();
            LocalDate date = LocalDate.of(2027, 6, 12);
            DemandeInitieeDto demande = new DemandeInitieeDto(prestataireId, "Bonjour");
            List<RubriqueDto> rubriques = List.of(
                    new RubriqueDto("lieu", List.of()),
                    new RubriqueDto("musique-animation", List.of(demande)));
            Utilisateur utilisateur = mock(Utilisateur.class);
            EvenementDto expected = new EvenementDto(
                    "mariage", null, null, null, date, "Lyon", null, null, rubriques);
            when(templateService.getEventRubriquesFromDemande("mariage", demande)).thenReturn(rubriques);
            when(evenementMapper.toEvenement(expected, utilisateur)).thenReturn(Evenement.builder().build());
            when(evenementRepository.save(any())).thenAnswer(invocation -> {
                Evenement e = invocation.getArgument(0);
                e.setId(EVENT_ID);
                return e;
            });

            CreateEventResponse response = evenementService.createEventFromDemande(utilisateur,
                    new CreateEventInConnectedFlowDemandeRequest(prestataireId, "mariage", null, null, null, date, "Lyon", null, null, "Bonjour"));

            assertThat(response.eventId()).isEqualTo(EVENT_ID);
            verify(evenementMapper).toEvenement(expected, utilisateur);
        }
    }

    // ── CreateFromDto ─────────────────────────────────────────────────────────

    @Nested
    class CreateFromDto {

        @Test
        void givenEvenementDto_whenCreateFromDto_thenSavesTheMappedEventWithItsDefaultTitle() {
            Utilisateur utilisateur = mock(Utilisateur.class);
            EvenementDto evenementDto = new EvenementDto(
                    "mariage", null, null, null, null, "Lyon", null, null, List.of());
            Evenement mapped = Evenement.builder().ville("Lyon").build();
            when(evenementMapper.toEvenement(evenementDto, utilisateur)).thenReturn(mapped);
            when(evenementRepository.save(mapped)).thenReturn(mapped);

            Evenement result = evenementService.createFromDto(utilisateur, evenementDto);

            assertThat(result).isSameAs(mapped);
            assertThat(result.getTitle()).isEqualTo("Mon événement");
        }

        @Test
        void givenDemandesInSeveralRubriques_whenCreateFromDto_thenCreatesOneReservationPerDemandeInItsRubrique() {
            Utilisateur utilisateur = mock(Utilisateur.class);
            LocalDate date = LocalDate.of(2027, 6, 12);
            UUID djId = UUID.randomUUID();
            UUID traiteurId = UUID.randomUUID();
            Prestataire dj = mock(Prestataire.class);
            Prestataire traiteur = mock(Prestataire.class);
            EvenementDto evenementDto = new EvenementDto("mariage", null, null, null, date, "Lyon", null, null,
                    List.of(new RubriqueDto("restauration", List.of(new DemandeInitieeDto(traiteurId, "Pour 80 ?"))),
                            new RubriqueDto("musique-animation", List.of(new DemandeInitieeDto(djId, "La soirée ?"))),
                            new RubriqueDto("lieu", List.of())));
            Evenement saved = Evenement.builder().id(EVENT_ID).utilisateur(utilisateur).date(date).build();
            when(evenementMapper.toEvenement(evenementDto, utilisateur)).thenReturn(saved);
            when(evenementRepository.save(saved)).thenReturn(saved);
            when(prestataireService.getById(djId)).thenReturn(dj);
            when(prestataireService.getById(traiteurId)).thenReturn(traiteur);

            evenementService.createFromDto(utilisateur, evenementDto);

            verify(reservationService).create(saved, traiteur, utilisateur, date, "Pour 80 ?", "restauration");
            verify(reservationService).create(saved, dj, utilisateur, date, "La soirée ?", "musique-animation");
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
}
