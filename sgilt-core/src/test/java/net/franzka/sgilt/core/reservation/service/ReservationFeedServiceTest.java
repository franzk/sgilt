package net.franzka.sgilt.core.reservation.service;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Document;
import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationFeed;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.event.ActorRole;
import net.franzka.sgilt.core.reservation.event.FeedItemType;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.reservation.event.reservationfeeditemadded.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.core.reservation.exception.ReservationFeedItemNotFoundException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotAllowedException;
import net.franzka.sgilt.core.reservation.mapper.ReservationFeedMapper;
import net.franzka.sgilt.core.reservation.repository.ReservationFeedRepository;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.storage.FileStreamResult;
import net.franzka.sgilt.core.storage.FileTooLargeException;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationFeedServiceTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationFeedRepository feedRepository;

    @Mock
    private ReservationFeedMapper feedMapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ReservationEventMapper reservationEventMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ReservationFeedService reservationFeedService;

    private final UUID reservationId = UUID.randomUUID();

    // -------------------------------------------------------------------------
    // addNote
    // -------------------------------------------------------------------------

    @Nested
    class AddNote {

        @Test
        void givenNonPersonalNoteFromPrestataire_whenAddNote_thenPublishesEventForClient() {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(feedRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(feedMapper.toFeedItem(any(), eq(false), eq(reservationId))).thenReturn(FeedItemDto.builder().build());

            ReservationFeedItemAddedEvent mappedEvent = feedItemAddedEvent(FeedItemType.NOTE);
            when(reservationEventMapper.toFeedItemAddedEventForClient(reservation, FeedItemType.NOTE)).thenReturn(mappedEvent);

            reservationFeedService.addNote(reservationId, FeedCaller.PRESTATAIRE, null,
                    new AddNoteRequest("Titre", "Contenu", false));

            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }

        @Test
        void givenNonPersonalNoteFromClient_whenAddNote_thenPublishesEventForPro() {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Utilisateur client = Utilisateur.builder().id(UUID.randomUUID()).build();
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(feedRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(feedMapper.toFeedItem(any(), eq(false), eq(reservationId))).thenReturn(FeedItemDto.builder().build());

            ReservationFeedItemAddedEvent mappedEvent = feedItemAddedEvent(FeedItemType.NOTE);
            when(reservationEventMapper.toFeedItemAddedEventForPro(reservation, FeedItemType.NOTE)).thenReturn(mappedEvent);

            reservationFeedService.addNote(reservationId, FeedCaller.CLIENT, client,
                    new AddNoteRequest("Titre", "Contenu", false));

            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }

        @Test
        void givenPersonalNote_whenAddNote_thenDoesNotPublishEvent() {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(feedRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(feedMapper.toFeedItem(any(), eq(false), eq(reservationId))).thenReturn(FeedItemDto.builder().build());

            reservationFeedService.addNote(reservationId, FeedCaller.PRESTATAIRE, null,
                    new AddNoteRequest("Titre", "Contenu", true));

            verify(applicationEventPublisher, never()).publishEvent(any());
        }
    }

    // -------------------------------------------------------------------------
    // addDocument
    // -------------------------------------------------------------------------

    @Nested
    class AddDocument {

        @Test
        void givenNonPersonalDocumentFromPrestataire_whenAddDocument_thenPublishesEventForClient() throws Exception {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(fileStorageService.uploadDocument(any(), any())).thenReturn("path/to/file");
            when(feedRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(feedMapper.toFeedItem(any(), eq(false), eq(reservationId))).thenReturn(FeedItemDto.builder().build());

            ReservationFeedItemAddedEvent mappedEvent = feedItemAddedEvent(FeedItemType.DOCUMENT);
            when(reservationEventMapper.toFeedItemAddedEventForClient(reservation, FeedItemType.DOCUMENT)).thenReturn(mappedEvent);

            var file = new MockMultipartFile("file", "devis.pdf", "application/pdf", "contenu".getBytes());
            reservationFeedService.addDocument(reservationId, FeedCaller.PRESTATAIRE, null, file, false);

            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }

        @Test
        void givenPersonalDocument_whenAddDocument_thenDoesNotPublishEvent() throws Exception {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(fileStorageService.uploadDocument(any(), any())).thenReturn("path/to/file");
            when(feedRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(feedMapper.toFeedItem(any(), eq(false), eq(reservationId))).thenReturn(FeedItemDto.builder().build());

            var file = new MockMultipartFile("file", "devis.pdf", "application/pdf", "contenu".getBytes());
            reservationFeedService.addDocument(reservationId, FeedCaller.PRESTATAIRE, null, file, true);

            verify(applicationEventPublisher, never()).publishEvent(any());
        }
    }

    // -------------------------------------------------------------------------
    // getFeed
    // -------------------------------------------------------------------------

    @Nested
    class GetFeed {

        @Test
        void givenCaller_whenGetFeed_thenReturnsMappedVisibleItems() {
            Note note = Note.builder().id(UUID.randomUUID()).build();
            List<ReservationFeed> visible = List.of(note);
            List<FeedItemDto> mapped = List.of(FeedItemDto.builder().build());
            when(feedRepository.findVisible(reservationId, FeedCaller.CLIENT)).thenReturn(visible);
            when(feedMapper.toFeedItems(visible, reservationId)).thenReturn(mapped);

            assertThat(reservationFeedService.getFeed(reservationId, FeedCaller.CLIENT)).isEqualTo(mapped);
        }
    }

    // -------------------------------------------------------------------------
    // addDocument — cas particuliers
    // -------------------------------------------------------------------------

    @Nested
    class AddDocumentEdgeCases {

        @Test
        void givenFileTooLarge_whenAddDocument_thenThrowsFileTooLargeWithoutTouchingReservation() {
            var file = new MockMultipartFile("file", "gros.pdf", "application/pdf", new byte[11 * 1024 * 1024]);

            assertThatThrownBy(() -> reservationFeedService.addDocument(reservationId, FeedCaller.CLIENT, null, file, false))
                    .isInstanceOf(FileTooLargeException.class);
            verify(reservationService, never()).getReservationById(any());
        }

        @Test
        void givenUploadIOException_whenAddDocument_thenThrowsFileStorageException() throws IOException {
            Reservation reservation = Reservation.builder().id(reservationId).build();
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(fileStorageService.uploadDocument(any(), any())).thenThrow(new IOException("erreur R2"));

            var file = new MockMultipartFile("file", "devis.pdf", "application/pdf", "contenu".getBytes());

            assertThatThrownBy(() -> reservationFeedService.addDocument(reservationId, FeedCaller.CLIENT, null, file, false))
                    .isInstanceOf(FileStorageException.class);
        }

        @Test
        void givenNoOriginalFilename_whenAddDocument_thenFallsBackToDefaultName() throws IOException {
            // MockMultipartFile convertit un originalFilename null en "" : on mocke MultipartFile
            // directement pour simuler une vraie absence de filename dans le Content-Disposition.
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Utilisateur client = Utilisateur.builder().id(UUID.randomUUID()).build();
            MultipartFile file = mock(MultipartFile.class);
            when(file.getSize()).thenReturn(7L);
            when(file.getOriginalFilename()).thenReturn(null);
            when(file.getContentType()).thenReturn("application/pdf");
            when(reservationService.getReservationById(reservationId)).thenReturn(reservation);
            when(fileStorageService.uploadDocument(any(), any())).thenReturn("path/to/file");
            when(feedMapper.toFeedItem(any(), eq(false), eq(reservationId))).thenReturn(FeedItemDto.builder().build());

            reservationFeedService.addDocument(reservationId, FeedCaller.CLIENT, client, file, false);

            var captor = org.mockito.ArgumentCaptor.forClass(Document.class);
            verify(feedRepository).save(captor.capture());
            assertThat(captor.getValue().getFileName()).isEqualTo("document");
        }
    }

    // -------------------------------------------------------------------------
    // streamDocument
    // -------------------------------------------------------------------------

    @Nested
    class StreamDocument {

        @Test
        void givenExistingDocumentInReservation_whenStreamDocument_thenReturnsStreamResult() throws IOException {
            UUID documentId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Document document = Document.builder().id(documentId).reservation(reservation)
                    .fileName("devis.pdf").filePath("path/devis.pdf").mimeType("application/pdf").build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));
            var stream = new ByteArrayInputStream("contenu".getBytes());
            when(fileStorageService.streamDocument("path/devis.pdf")).thenReturn(stream);

            FileStreamResult result = reservationFeedService.streamDocument(reservationId, documentId);

            assertThat(result.fileName()).isEqualTo("devis.pdf");
            assertThat(result.mimeType()).isEqualTo("application/pdf");
            assertThat(result.inputStream()).isEqualTo(stream);
        }

        @Test
        void givenUnknownDocument_whenStreamDocument_thenThrowsNotFound() {
            UUID documentId = UUID.randomUUID();
            when(feedRepository.findById(documentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationFeedService.streamDocument(reservationId, documentId))
                    .isInstanceOf(ReservationFeedItemNotFoundException.class);
        }

        @Test
        void givenDocumentBelongsToAnotherReservation_whenStreamDocument_thenThrowsNotFound() {
            UUID documentId = UUID.randomUUID();
            Reservation otherReservation = Reservation.builder().id(UUID.randomUUID()).build();
            Document document = Document.builder().id(documentId).reservation(otherReservation).build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));

            assertThatThrownBy(() -> reservationFeedService.streamDocument(reservationId, documentId))
                    .isInstanceOf(ReservationFeedItemNotFoundException.class);
        }

        @Test
        void givenFeedItemIsNote_whenStreamDocument_thenThrowsNotFound() {
            UUID noteId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Note note = Note.builder().id(noteId).reservation(reservation).build();
            when(feedRepository.findById(noteId)).thenReturn(Optional.of(note));

            assertThatThrownBy(() -> reservationFeedService.streamDocument(reservationId, noteId))
                    .isInstanceOf(ReservationFeedItemNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // deleteDocument
    // -------------------------------------------------------------------------

    @Nested
    class DeleteDocument {

        @Test
        void givenUtilisateurAuthor_whenDeleteDocument_thenDeletesFromStorageAndMarksDeleted() throws IOException {
            UUID documentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Utilisateur author = Utilisateur.builder().id(userId).build();
            Document document = Document.builder().id(documentId).reservation(reservation)
                    .utilisateur(author).filePath("path/devis.pdf").build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));

            reservationFeedService.deleteDocument(reservationId, documentId, userId);

            verify(fileStorageService).deleteDocument("path/devis.pdf");
            assertThat(document.getDeletedAt()).isNotNull();
            verify(feedRepository).save(document);
        }

        @Test
        void givenPrestataireAuthor_whenDeleteDocument_thenResolvesAuthorFromPrestataireUtilisateur() throws IOException {
            UUID documentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Utilisateur prestataireUtilisateur = Utilisateur.builder().id(userId).build();
            Prestataire prestataire = Prestataire.builder().utilisateur(prestataireUtilisateur).build();
            Document document = Document.builder().id(documentId).reservation(reservation)
                    .prestataire(prestataire).filePath("path/devis.pdf").build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));

            reservationFeedService.deleteDocument(reservationId, documentId, userId);

            verify(fileStorageService).deleteDocument("path/devis.pdf");
        }

        @Test
        void givenNotAuthor_whenDeleteDocument_thenThrowsNotAllowedWithoutDeleting() {
            UUID documentId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Utilisateur author = Utilisateur.builder().id(UUID.randomUUID()).build();
            Document document = Document.builder().id(documentId).reservation(reservation).utilisateur(author).build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));

            assertThatThrownBy(() -> reservationFeedService.deleteDocument(reservationId, documentId, UUID.randomUUID()))
                    .isInstanceOf(ReservationNotAllowedException.class);
        }

        @Test
        void givenUnknownDocument_whenDeleteDocument_thenThrowsNotFound() {
            UUID documentId = UUID.randomUUID();
            when(feedRepository.findById(documentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationFeedService.deleteDocument(reservationId, documentId, UUID.randomUUID()))
                    .isInstanceOf(ReservationFeedItemNotFoundException.class);
        }

        @Test
        void givenDocumentBelongsToAnotherReservation_whenDeleteDocument_thenThrowsNotFound() {
            UUID documentId = UUID.randomUUID();
            Reservation otherReservation = Reservation.builder().id(UUID.randomUUID()).build();
            Document document = Document.builder().id(documentId).reservation(otherReservation).build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));

            assertThatThrownBy(() -> reservationFeedService.deleteDocument(reservationId, documentId, UUID.randomUUID()))
                    .isInstanceOf(ReservationFeedItemNotFoundException.class);
        }

        @Test
        void givenStorageDeletionFails_whenDeleteDocument_thenThrowsFileStorageException() throws IOException {
            UUID documentId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).build();
            Utilisateur author = Utilisateur.builder().id(userId).build();
            Document document = Document.builder().id(documentId).reservation(reservation)
                    .utilisateur(author).filePath("path/devis.pdf").build();
            when(feedRepository.findById(documentId)).thenReturn(Optional.of(document));
            doThrow(new IOException("erreur R2")).when(fileStorageService).deleteDocument("path/devis.pdf");

            assertThatThrownBy(() -> reservationFeedService.deleteDocument(reservationId, documentId, userId))
                    .isInstanceOf(FileStorageException.class);
        }
    }

    private ReservationFeedItemAddedEvent feedItemAddedEvent(FeedItemType itemType) {
        return new ReservationFeedItemAddedEvent(
                reservationId, UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                itemType, "Studio Fleur", ActorRole.PRO, "Anniversaire de Paul", LocalDate.now());
    }
}
