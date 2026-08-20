package net.franzka.sgilt.core.reservation.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.reservation.event.ActorRole;
import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEvent;
import net.franzka.sgilt.core.reservation.event.reservationstatuschanged.ReservationStatusChangedEvent;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.AdminReservationListItemDto;
import net.franzka.sgilt.core.reservation.dto.RefuseReservationRequest;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.event.mapper.ReservationEventMapper;
import net.franzka.sgilt.core.reservation.mapper.ReservationMapper;
import net.franzka.sgilt.core.reservation.repository.NoteRepository;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationEventMapper reservationEventMapper;

    @InjectMocks
    private ReservationService reservationService;

    // -------------------------------------------------------------------------
    // getCountsForEvenement
    // -------------------------------------------------------------------------

    @Nested
    class GetCountsForEvenement {

        @Test
        void givenNoReservations_whenGetCountsForEvenement_thenReturnsZeroCounts() {
            when(reservationRepository.findByEvenementId(EVENT_ID)).thenReturn(List.of());

            ReservationCounts result = reservationService.getCountsForEvenement(EVENT_ID);

            assertThat(result.confirmedCount()).isZero();
            assertThat(result.inDiscussionCount()).isZero();
        }

        @Test
        void givenOnlyConfirmed_whenGetCountsForEvenement_thenReturnsCorrectCounts() {
            when(reservationRepository.findByEvenementId(EVENT_ID)).thenReturn(List.of(
                    reservationWith(ReservationStatus.CONFIRMED),
                    reservationWith(ReservationStatus.CONFIRMED)
            ));

            ReservationCounts result = reservationService.getCountsForEvenement(EVENT_ID);

            assertThat(result.confirmedCount()).isEqualTo(2);
            assertThat(result.inDiscussionCount()).isZero();
        }

        @Test
        void givenOnlyInDiscussion_whenGetCountsForEvenement_thenReturnsCorrectCounts() {
            when(reservationRepository.findByEvenementId(EVENT_ID)).thenReturn(List.of(
                    reservationWith(ReservationStatus.IN_DISCUSSION),
                    reservationWith(ReservationStatus.IN_DISCUSSION),
                    reservationWith(ReservationStatus.IN_DISCUSSION)
            ));

            ReservationCounts result = reservationService.getCountsForEvenement(EVENT_ID);

            assertThat(result.confirmedCount()).isZero();
            assertThat(result.inDiscussionCount()).isEqualTo(3);
        }

        @Test
        void givenMixedStatuses_whenGetCountsForEvenement_thenCountsOnlyRelevantStatuses() {
            when(reservationRepository.findByEvenementId(EVENT_ID)).thenReturn(List.of(
                    reservationWith(ReservationStatus.CONFIRMED),
                    reservationWith(ReservationStatus.CONFIRMED),
                    reservationWith(ReservationStatus.IN_DISCUSSION),
                    reservationWith(ReservationStatus.NEW),
                    reservationWith(ReservationStatus.REFUSED_PRE_CONTACT)
            ));

            ReservationCounts result = reservationService.getCountsForEvenement(EVENT_ID);

            assertThat(result.confirmedCount()).isEqualTo(2);
            assertThat(result.inDiscussionCount()).isEqualTo(1);
        }
    }

    // -------------------------------------------------------------------------
    // getStatusCountsByPrestataire
    // -------------------------------------------------------------------------

    @Nested
    class GetStatusCountsByPrestataire {

        private final UUID prestataireId = UUID.randomUUID();

        @Test
        void givenNoReservations_whenGetStatusCountsByPrestataire_thenReturnsEmptyMap() {
            when(reservationRepository.findByPrestataireId(prestataireId)).thenReturn(List.of());

            Map<ReservationStatus, Integer> result = reservationService.getStatusCountsByPrestataire(prestataireId);

            assertThat(result).isEmpty();
        }

        @Test
        void givenMixedStatuses_whenGetStatusCountsByPrestataire_thenGroupsByStatus() {
            when(reservationRepository.findByPrestataireId(prestataireId)).thenReturn(List.of(
                    reservationWith(ReservationStatus.NEW),
                    reservationWith(ReservationStatus.NEW),
                    reservationWith(ReservationStatus.IN_DISCUSSION)
            ));

            Map<ReservationStatus, Integer> result = reservationService.getStatusCountsByPrestataire(prestataireId);

            assertThat(result)
                    .containsEntry(ReservationStatus.NEW, 2)
                    .containsEntry(ReservationStatus.IN_DISCUSSION, 1);
        }
    }

    // -------------------------------------------------------------------------
    // prestataireAReservationSurEvenement
    // -------------------------------------------------------------------------

    @Nested
    class PrestataireAReservationSurEvenement {

        private static final UUID PRESTATAIRE_USER_ID = UUID.randomUUID();

        @Test
        void givenReservationExists_whenPrestataireAReservationSurEvenement_thenReturnsTrue() {
            when(reservationRepository.existsByEvenementIdAndPrestataireUtilisateurId(EVENT_ID, PRESTATAIRE_USER_ID))
                    .thenReturn(true);

            assertThat(reservationService.prestataireAReservationSurEvenement(EVENT_ID, PRESTATAIRE_USER_ID)).isTrue();
        }

        @Test
        void givenNoReservation_whenPrestataireAReservationSurEvenement_thenReturnsFalse() {
            when(reservationRepository.existsByEvenementIdAndPrestataireUtilisateurId(EVENT_ID, PRESTATAIRE_USER_ID))
                    .thenReturn(false);

            assertThat(reservationService.prestataireAReservationSurEvenement(EVENT_ID, PRESTATAIRE_USER_ID)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Nested
    class Create {

        @Test
        void givenReservationCreated_whenCreate_thenMappedEventPublished() {
            Utilisateur prestataireUtilisateur = Utilisateur.builder()
                    .id(UUID.randomUUID())
                    .email("presta@example.com")
                    .build();
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID())
                    .utilisateur(prestataireUtilisateur)
                    .build();
            Utilisateur client = Utilisateur.builder()
                    .id(UUID.randomUUID())
                    .firstName("Sophie")
                    .lastName("Leroy")
                    .build();
            Evenement evenement = Evenement.builder()
                    .id(UUID.randomUUID())
                    .title("Anniversaire de Paul")
                    .build();
            LocalDate date = LocalDate.now();

            when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
                Reservation reservation = invocation.getArgument(0);
                reservation.setId(UUID.randomUUID());
                return reservation;
            });
            ReservationCreatedEvent mappedEvent = new ReservationCreatedEvent(
                    UUID.randomUUID(), prestataireUtilisateur.getId(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", date);
            when(reservationEventMapper.toReservationCreatedEvent(any(Reservation.class))).thenReturn(mappedEvent);

            reservationService.create(evenement, prestataire, client, date, "Un message");

            ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationEventMapper).toReservationCreatedEvent(reservationCaptor.capture());
            assertThat(reservationCaptor.getValue().getPrestataire()).isEqualTo(prestataire);
            assertThat(reservationCaptor.getValue().getUtilisateur()).isEqualTo(client);
            assertThat(reservationCaptor.getValue().getEvenement()).isEqualTo(evenement);

            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }
    }

    // -------------------------------------------------------------------------
    // markContacted
    // -------------------------------------------------------------------------

    @Nested
    class MarkContacted {

        @Test
        void givenReservationNew_whenMarkContacted_thenMappedEventPublishedToPro() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.NEW)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            ReservationStatusChangedEvent mappedEvent = new ReservationStatusChangedEvent(
                    reservationId, UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    ReservationStatus.IN_DISCUSSION, "Sophie Leroy", ActorRole.USER,
                    "Anniversaire de Paul", LocalDate.now());
            when(reservationEventMapper.toStatusChangedEventForPro(reservation, ReservationStatus.IN_DISCUSSION))
                    .thenReturn(mappedEvent);

            reservationService.markContacted(reservationId);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.IN_DISCUSSION);
            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }
    }

    // -------------------------------------------------------------------------
    // confirm
    // -------------------------------------------------------------------------

    @Nested
    class Confirm {

        @Test
        void givenReservationInDiscussion_whenConfirm_thenMappedEventPublishedToPro() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.IN_DISCUSSION)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            ReservationStatusChangedEvent mappedEvent = new ReservationStatusChangedEvent(
                    reservationId, UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    ReservationStatus.CONFIRMED, "Sophie Leroy", ActorRole.USER,
                    "Anniversaire de Paul", LocalDate.now());
            when(reservationEventMapper.toStatusChangedEventForPro(reservation, ReservationStatus.CONFIRMED))
                    .thenReturn(mappedEvent);

            reservationService.confirm(reservationId);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }
    }

    // -------------------------------------------------------------------------
    // refuse
    // -------------------------------------------------------------------------

    @Nested
    class Refuse {

        @Test
        void givenReservationNew_whenRefuse_thenMappedEventPublished() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.NEW)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            ReservationStatusChangedEvent mappedEvent = statusChangedEvent(reservationId, ReservationStatus.REFUSED_PRE_CONTACT);
            when(reservationEventMapper.toStatusChangedEventForClient(reservation,ReservationStatus.REFUSED_PRE_CONTACT))
                    .thenReturn(mappedEvent);

            reservationService.refuse(reservationId, new RefuseReservationRequest("Indisponible"));

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REFUSED_PRE_CONTACT);
            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }
    }

    // -------------------------------------------------------------------------
    // cancelByPro
    // -------------------------------------------------------------------------

    @Nested
    class CancelByPro {

        @Test
        void givenReservationConfirmed_whenCancelByPro_thenMappedEventPublished() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            ReservationStatusChangedEvent mappedEvent = statusChangedEvent(reservationId, ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION);
            when(reservationEventMapper.toStatusChangedEventForClient(reservation, ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION))
                    .thenReturn(mappedEvent);

            reservationService.cancelByPro(reservationId, "Indisponible finalement", false);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION);
            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }

        @Test
        void givenIsPersonalTrue_whenCancelByPro_thenNoteSavedAsPersonal() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
            when(reservationEventMapper.toStatusChangedEventForClient(reservation, ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION))
                    .thenReturn(statusChangedEvent(reservationId, ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION));

            reservationService.cancelByPro(reservationId, "Motif confidentiel", true);

            ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
            verify(noteRepository).save(noteCaptor.capture());
            assertThat(noteCaptor.getValue().getIsPersonal()).isTrue();
            assertThat(noteCaptor.getValue().getContent()).isEqualTo("Motif confidentiel");
        }
    }

    // -------------------------------------------------------------------------
    // cancel
    // -------------------------------------------------------------------------

    @Nested
    class Cancel {

        @Test
        void givenReservationNew_whenCancel_thenMappedEventPublished() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.NEW)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            ReservationStatusChangedEvent mappedEvent = new ReservationStatusChangedEvent(
                    reservationId, UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT, "Sophie Leroy", ActorRole.USER,
                    "Anniversaire de Paul", LocalDate.now());
            when(reservationEventMapper.toStatusChangedEventForPro(reservation, ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT))
                    .thenReturn(mappedEvent);

            reservationService.cancel(reservationId, null, false);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT);
            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }

        @Test
        void givenReservationConfirmed_whenCancel_thenMappedEventPublished() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            ReservationStatusChangedEvent mappedEvent = new ReservationStatusChangedEvent(
                    reservationId, UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION, "Sophie Leroy", ActorRole.USER,
                    "Anniversaire de Paul", LocalDate.now());
            when(reservationEventMapper.toStatusChangedEventForPro(reservation, ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION))
                    .thenReturn(mappedEvent);

            reservationService.cancel(reservationId, "Le prestataire ne répond plus", false);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION);
            verify(applicationEventPublisher).publishEvent(mappedEvent);
        }
    }

    // -------------------------------------------------------------------------
    // getAdminReservations
    // -------------------------------------------------------------------------

    @Nested
    class GetAdminReservations {

        @Test
        void givenNoStatusFilter_whenGetAdminReservations_thenListsAllOrderedByCreatedAtDesc() {
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            AdminReservationListItemDto dto = new AdminReservationListItemDto(
                    reservation.getId(), "Anniversaire de Paul", "client@example.com",
                    "presta@example.com", "studio-fleur", ReservationStatus.NEW, LocalDateTime.now());
            when(reservationRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(reservation));
            when(reservationMapper.toAdminListItemDto(reservation)).thenReturn(dto);

            List<AdminReservationListItemDto> result = reservationService.getAdminReservations(null);

            assertThat(result).containsExactly(dto);
        }

        @Test
        void givenStatusFilter_whenGetAdminReservations_thenListsFilteredByStatus() {
            Reservation reservation = reservationWith(ReservationStatus.CONFIRMED);
            AdminReservationListItemDto dto = new AdminReservationListItemDto(
                    reservation.getId(), "Anniversaire de Paul", "client@example.com",
                    "presta@example.com", "studio-fleur", ReservationStatus.CONFIRMED, LocalDateTime.now());
            when(reservationRepository.findByStatusOrderByCreatedAtDesc(ReservationStatus.CONFIRMED))
                    .thenReturn(List.of(reservation));
            when(reservationMapper.toAdminListItemDto(reservation)).thenReturn(dto);

            List<AdminReservationListItemDto> result = reservationService.getAdminReservations(ReservationStatus.CONFIRMED);

            assertThat(result).containsExactly(dto);
        }
    }

    private ReservationStatusChangedEvent statusChangedEvent(UUID reservationId, ReservationStatus status) {
        return new ReservationStatusChangedEvent(
                reservationId, UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                status, "Studio Fleur", ActorRole.PRO, "Anniversaire de Paul", LocalDate.now());
    }

    private Reservation reservationWith(ReservationStatus status) {
        return Reservation.builder().id(UUID.randomUUID()).status(status).build();
    }
}
