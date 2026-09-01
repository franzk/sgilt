package net.franzka.sgilt.core.evenement.controller;

import net.franzka.sgilt.core.evenement.dto.AddReservationRequest;
import net.franzka.sgilt.core.evenement.dto.CoverSelectDto;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.CreateEventRequest;
import net.franzka.sgilt.core.evenement.dto.CreateEventResponse;
import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.JournalEvenementDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.evenement.service.JournalEvenementService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvenementControllerTest {

    @Mock
    private EvenementService evenementService;

    @Mock
    private JournalEvenementService journalEvenementService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private EvenementController controller;

    private final UUID userId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();
    private final Utilisateur utilisateur = Utilisateur.builder().id(userId).build();

    // -------------------------------------------------------------------------
    // createEvent
    // -------------------------------------------------------------------------

    @Nested
    class CreateEvent {

        @Test
        void givenValidRequest_whenCreateEvent_thenReturns201() {
            when(currentUserService.get()).thenReturn(utilisateur);
            CreateEventRequest body = mock(CreateEventRequest.class);
            CreateEventResponse response = new CreateEventResponse(eventId);
            when(evenementService.createEvent(utilisateur, body)).thenReturn(response);

            ResponseEntity<CreateEventResponse> result = controller.createEvent(body);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody()).isEqualTo(response);
        }
    }

    // -------------------------------------------------------------------------
    // getMyEvents
    // -------------------------------------------------------------------------

    @Nested
    class GetMyEvents {

        @Test
        void givenCurrentUser_whenGetMyEvents_thenReturnsUserEvents() {
            when(currentUserService.getId()).thenReturn(userId);
            List<EvenementSummaryDto> events = List.of(mock(EvenementSummaryDto.class));
            when(evenementService.getUserEvents(userId)).thenReturn(events);

            assertThat(controller.getMyEvents().getBody()).isEqualTo(events);
        }
    }

    // -------------------------------------------------------------------------
    // getEventDetail
    // -------------------------------------------------------------------------

    @Nested
    class GetEventDetail {

        @Test
        void givenEventId_whenGetEventDetail_thenReturnsDetail() {
            when(currentUserService.getId()).thenReturn(userId);
            EventDetailDto dto = mock(EventDetailDto.class);
            when(evenementService.getEventDetail(eventId, userId)).thenReturn(dto);

            assertThat(controller.getEventDetail(eventId).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // patchEvent
    // -------------------------------------------------------------------------

    @Nested
    class PatchEvent {

        @Test
        void givenPatch_whenPatchEvent_thenDelegatesAndReturnsDetail() {
            when(currentUserService.getId()).thenReturn(userId);
            EventPatchDto patch = new EventPatchDto(null, "Lyon", null, null, null, null, null, null, null);
            EventDetailDto dto = mock(EventDetailDto.class);
            when(evenementService.patchEvent(eventId, userId, patch)).thenReturn(dto);

            assertThat(controller.patchEvent(eventId, patch).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getEventCounts
    // -------------------------------------------------------------------------

    @Nested
    class GetEventCounts {

        @Test
        void givenEventId_whenGetEventCounts_thenReturnsCounts() {
            when(currentUserService.getId()).thenReturn(userId);
            EventCountsDto dto = mock(EventCountsDto.class);
            when(evenementService.getEventCounts(eventId, userId)).thenReturn(dto);

            assertThat(controller.getEventCounts(eventId).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getJournal
    // -------------------------------------------------------------------------

    @Nested
    class GetJournal {

        @Test
        void givenAuthorizedAccess_whenGetJournal_thenVerifiesAccessBeforeReturningPage() {
            when(currentUserService.getId()).thenReturn(userId);
            @SuppressWarnings("unchecked")
            Page<JournalEvenementDto> page = mock(Page.class);
            when(journalEvenementService.getPage(eventId, 0)).thenReturn(page);

            ResponseEntity<Page<JournalEvenementDto>> result = controller.getJournal(eventId, 0);

            assertThat(result.getBody()).isEqualTo(page);
            verify(evenementService).verifierAccesLectureJournal(eventId, userId);
        }
    }

    // -------------------------------------------------------------------------
    // uploadCover
    // -------------------------------------------------------------------------

    @Nested
    class UploadCover {

        @Test
        void givenFile_whenUploadCover_thenReturnsCoverUrl() {
            when(currentUserService.getId()).thenReturn(userId);
            var file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
            CoverUrlDto dto = new CoverUrlDto("uploads/photo.jpg");
            when(evenementService.updateCover(eventId, userId, file)).thenReturn(dto);

            assertThat(controller.uploadCover(eventId, file).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // selectCover
    // -------------------------------------------------------------------------

    @Nested
    class SelectCover {

        @Test
        void givenImagePath_whenSelectCover_thenDelegatesToService() {
            when(currentUserService.getId()).thenReturn(userId);
            CoverSelectDto body = new CoverSelectDto("bank/plage.jpg");
            CoverUrlDto dto = new CoverUrlDto("bank/plage.jpg");
            when(evenementService.selectCover(eventId, userId, "bank/plage.jpg")).thenReturn(dto);

            assertThat(controller.selectCover(eventId, body).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // addReservation
    // -------------------------------------------------------------------------

    @Nested
    class AddReservation {

        @Test
        void givenRequest_whenAddReservation_thenDelegatesAndReturns201() {
            when(currentUserService.get()).thenReturn(utilisateur);
            UUID prestataireId = UUID.randomUUID();
            AddReservationRequest body = new AddReservationRequest(prestataireId, "Bonjour");

            ResponseEntity<Void> result = controller.addReservation(eventId, body);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            verify(evenementService).addReservation(eventId, utilisateur, prestataireId, "Bonjour");
        }
    }
}
