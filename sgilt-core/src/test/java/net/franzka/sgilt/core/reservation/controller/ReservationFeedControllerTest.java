package net.franzka.sgilt.core.reservation.controller;

import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.service.ReservationFeedService;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.storage.FileStreamResult;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationFeedControllerTest {

    @Mock
    private ReservationFeedService reservationFeedService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ReservationFeedController controller;

    private final UUID userId = UUID.randomUUID();
    private final UUID reservationId = UUID.randomUUID();

    // -------------------------------------------------------------------------
    // getFeed
    // -------------------------------------------------------------------------

    @Nested
    class GetFeed {

        @Test
        void givenClientCaller_whenGetFeed_thenVerifiesClientOwnershipAndReturnsFeed() {
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(false);
            List<FeedItemDto> feed = List.of(FeedItemDto.builder().build());
            when(reservationFeedService.getFeed(reservationId, FeedCaller.CLIENT)).thenReturn(feed);

            ResponseEntity<List<FeedItemDto>> response = controller.getFeed(reservationId);

            assertThat(response.getBody()).isEqualTo(feed);
            verify(reservationService).verifyOwnershipByReservationId(reservationId, userId);
        }

        @Test
        void givenPrestataireCaller_whenGetFeed_thenVerifiesProOwnershipAndReturnsFeed() {
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(true);
            List<FeedItemDto> feed = List.of(FeedItemDto.builder().build());
            when(reservationFeedService.getFeed(reservationId, FeedCaller.PRESTATAIRE)).thenReturn(feed);

            ResponseEntity<List<FeedItemDto>> response = controller.getFeed(reservationId);

            assertThat(response.getBody()).isEqualTo(feed);
            verify(reservationService).verifyProOwnershipByReservationId(reservationId, userId);
        }
    }

    // -------------------------------------------------------------------------
    // addNote
    // -------------------------------------------------------------------------

    @Nested
    class AddNote {

        @Test
        void givenClientCaller_whenAddNote_thenResolvesUtilisateurAndReturnsCreated() {
            Utilisateur utilisateur = Utilisateur.builder().id(userId).build();
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(false);
            when(currentUserService.get()).thenReturn(utilisateur);
            AddNoteRequest body = new AddNoteRequest("Titre", "Contenu", false);
            FeedItemDto item = FeedItemDto.builder().build();
            when(reservationFeedService.addNote(reservationId, FeedCaller.CLIENT, utilisateur, body)).thenReturn(item);

            ResponseEntity<FeedItemDto> response = controller.addNote(reservationId, body);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(item);
        }

        @Test
        void givenPrestataireCaller_whenAddNote_thenUtilisateurIsNull() {
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(true);
            AddNoteRequest body = new AddNoteRequest("Titre", "Contenu", false);
            FeedItemDto item = FeedItemDto.builder().build();
            when(reservationFeedService.addNote(reservationId, FeedCaller.PRESTATAIRE, null, body)).thenReturn(item);

            controller.addNote(reservationId, body);

            verify(reservationFeedService).addNote(reservationId, FeedCaller.PRESTATAIRE, null, body);
            verify(currentUserService, org.mockito.Mockito.never()).get();
        }
    }

    // -------------------------------------------------------------------------
    // addDocument
    // -------------------------------------------------------------------------

    @Nested
    class AddDocument {

        @Test
        void givenClientCaller_whenAddDocument_thenResolvesUtilisateurAndReturnsCreated() {
            Utilisateur utilisateur = Utilisateur.builder().id(userId).build();
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(false);
            when(currentUserService.get()).thenReturn(utilisateur);
            var file = new MockMultipartFile("file", "devis.pdf", "application/pdf", "contenu".getBytes());
            FeedItemDto item = FeedItemDto.builder().build();
            when(reservationFeedService.addDocument(reservationId, FeedCaller.CLIENT, utilisateur, file, false))
                    .thenReturn(item);

            ResponseEntity<FeedItemDto> response = controller.addDocument(reservationId, file, false);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(item);
        }
    }

    // -------------------------------------------------------------------------
    // streamDocument
    // -------------------------------------------------------------------------

    @Nested
    class StreamDocument {

        @Test
        void givenExistingDocument_whenStreamDocument_thenReturnsAttachmentWithResolvedMediaType() throws IOException {
            UUID documentId = UUID.randomUUID();
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(false);
            var stream = new ByteArrayInputStream("contenu".getBytes());
            FileStreamResult result = new FileStreamResult(stream, "devis.pdf", "application/pdf");
            when(reservationFeedService.streamDocument(reservationId, documentId)).thenReturn(result);

            ResponseEntity<InputStreamResource> response = controller.streamDocument(reservationId, documentId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("devis.pdf");
            verify(reservationService).verifyOwnershipByReservationId(reservationId, userId);
        }
    }

    // -------------------------------------------------------------------------
    // deleteDocument
    // -------------------------------------------------------------------------

    @Nested
    class DeleteDocument {

        @Test
        void givenPrestataireCaller_whenDeleteDocument_thenVerifiesProOwnershipAndDeletes() {
            UUID documentId = UUID.randomUUID();
            when(currentUserService.getId()).thenReturn(userId);
            when(currentUserService.isPro()).thenReturn(true);

            ResponseEntity<Void> response = controller.deleteDocument(reservationId, documentId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).verifyProOwnershipByReservationId(reservationId, userId);
            verify(reservationFeedService).deleteDocument(reservationId, documentId, userId);
        }
    }
}
