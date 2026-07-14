package net.franzka.sgilt.core.reservation.service;

import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.event.ActorRole;
import net.franzka.sgilt.core.reservation.event.FeedItemType;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.reservation.event.reservationfeeditemadded.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.core.reservation.mapper.ReservationFeedMapper;
import net.franzka.sgilt.core.reservation.repository.ReservationFeedRepository;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private ReservationFeedItemAddedEvent feedItemAddedEvent(FeedItemType itemType) {
        return new ReservationFeedItemAddedEvent(
                reservationId, UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                itemType, "Studio Fleur", ActorRole.PRO, "Anniversaire de Paul", LocalDate.now());
    }
}
