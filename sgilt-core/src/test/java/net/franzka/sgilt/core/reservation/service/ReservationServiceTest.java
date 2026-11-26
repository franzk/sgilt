package net.franzka.sgilt.core.reservation.service;

import net.franzka.sgilt.core.evenement.service.JournalEvenementService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationItemDto;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.AdminReservationListItemDto;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.RefuseReservationRequest;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.event.ReservationEventPublisher;
import net.franzka.sgilt.core.reservation.exception.InvalidStateException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotAllowedException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotFoundException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private ReservationEventPublisher reservationEventPublisher;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private JournalEvenementService journalEvenementService;

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

            reservationService.create(evenement, prestataire, client, date, "Un message", "musique-animation");

            ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationEventPublisher).publishCreated(reservationCaptor.capture());
            assertThat(reservationCaptor.getValue().getPrestataire()).isEqualTo(prestataire);
            assertThat(reservationCaptor.getValue().getUtilisateur()).isEqualTo(client);
            assertThat(reservationCaptor.getValue().getEvenement()).isEqualTo(evenement);
            assertThat(reservationCaptor.getValue().getRubriqueKey()).isEqualTo("musique-animation");
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

            reservationService.markContacted(reservationId);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.IN_DISCUSSION);
            verify(reservationEventPublisher).publishStatusChangedToPro(reservation);
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

            reservationService.confirm(reservationId);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
            verify(reservationEventPublisher).publishStatusChangedToPro(reservation);
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

            reservationService.refuse(reservationId, new RefuseReservationRequest("Indisponible"));

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REFUSED_PRE_CONTACT);
            verify(reservationEventPublisher).publishStatusChangedToClient(reservation);
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

            reservationService.cancelByPro(reservationId, "Indisponible finalement", false);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION);
            verify(reservationEventPublisher).publishStatusChangedToClient(reservation);
        }

        @Test
        void givenIsPersonalTrue_whenCancelByPro_thenNoteSavedAsPersonal() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

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

            reservationService.cancel(reservationId, null, false);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT);
            verify(reservationEventPublisher).publishStatusChangedToPro(reservation);
        }

        @Test
        void givenReservationConfirmed_whenCancel_thenMappedEventPublished() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder()
                    .id(reservationId)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            reservationService.cancel(reservationId, "Le prestataire ne répond plus", false);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION);
            verify(reservationEventPublisher).publishStatusChangedToPro(reservation);
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

    // -------------------------------------------------------------------------
    // getActiveReservations
    // -------------------------------------------------------------------------

    @Nested
    class GetActiveReservations {

        private final UUID userId = UUID.randomUUID();

        @Test
        void givenNewAndInDiscussion_whenGetActiveReservations_thenNewSortedFirst() {
            Reservation inDiscussion = reservationWith(ReservationStatus.IN_DISCUSSION);
            Reservation nouvelle = reservationWith(ReservationStatus.NEW);
            when(reservationRepository.findByUtilisateurIdAndStatusIn(
                    userId, List.of(ReservationStatus.NEW, ReservationStatus.IN_DISCUSSION)))
                    .thenReturn(List.of(inDiscussion, nouvelle));
            when(reservationRepository.existsByUtilisateurIdAndStatus(userId, ReservationStatus.CONFIRMED))
                    .thenReturn(true);
            ActiveReservationItemDto dtoInDiscussion = mock(ActiveReservationItemDto.class);
            ActiveReservationItemDto dtoNouvelle = mock(ActiveReservationItemDto.class);
            when(reservationMapper.toActiveItemDto(inDiscussion)).thenReturn(dtoInDiscussion);
            when(reservationMapper.toActiveItemDto(nouvelle)).thenReturn(dtoNouvelle);

            ActiveReservationsDto result = reservationService.getActiveReservations(userId);

            assertThat(result.items()).containsExactly(dtoNouvelle, dtoInDiscussion);
            assertThat(result.hasConfirmed()).isTrue();
        }

        @Test
        void givenNoConfirmedReservation_whenGetActiveReservations_thenHasConfirmedIsFalse() {
            when(reservationRepository.findByUtilisateurIdAndStatusIn(any(), any())).thenReturn(List.of());
            when(reservationRepository.existsByUtilisateurIdAndStatus(userId, ReservationStatus.CONFIRMED))
                    .thenReturn(false);

            assertThat(reservationService.getActiveReservations(userId).hasConfirmed()).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // getReservationSummaries
    // -------------------------------------------------------------------------

    @Nested
    class GetReservationSummaries {

        @Test
        void givenReservationsForEvenement_whenGetReservationSummaries_thenReturnsMappedList() {
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            ReservationSummaryDto dto = mock(ReservationSummaryDto.class);
            when(reservationRepository.findByEvenementId(EVENT_ID)).thenReturn(List.of(reservation));
            when(reservationMapper.toSummaryDto(reservation)).thenReturn(dto);

            assertThat(reservationService.getReservationSummaries(EVENT_ID)).containsExactly(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getMeta
    // -------------------------------------------------------------------------

    @Nested
    class GetMeta {

        @Test
        void givenExistingReservation_whenGetMeta_thenReturnsMappedDto() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            ReservationMetaDto dto = mock(ReservationMetaDto.class);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
            when(reservationMapper.toReservationMetaDto(reservation)).thenReturn(dto);

            assertThat(reservationService.getMeta(reservationId)).isEqualTo(dto);
        }

        @Test
        void givenUnknownReservation_whenGetMeta_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.getMeta(reservationId))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getProReservations / getProBoardCounts
    // -------------------------------------------------------------------------

    @Nested
    class GetProReservations {

        @Test
        void givenPrestataireUtilisateur_whenGetProReservations_thenReturnsMappedList() {
            UUID userId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            ProReservationSummaryDto dto = mock(ProReservationSummaryDto.class);
            when(reservationRepository.findByPrestataireUtilisateurIdOrderByStatus(userId)).thenReturn(List.of(reservation));
            when(reservationMapper.toProReservationSummaryDto(reservation)).thenReturn(dto);

            assertThat(reservationService.getProReservations(userId)).containsExactly(dto);
        }
    }

    @Nested
    class GetProReservationsToHandle {

        @Test
        void givenPrestataireUtilisateur_whenGetProReservationsToHandle_thenReturnsNewAndInDiscussion() {
            UUID userId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            ProReservationSummaryDto dto = mock(ProReservationSummaryDto.class);
            when(reservationRepository.findByPrestataireUtilisateurIdAndStatusInOrderByStatus(
                    userId, List.of(ReservationStatus.NEW, ReservationStatus.IN_DISCUSSION)))
                    .thenReturn(List.of(reservation));
            when(reservationMapper.toProReservationSummaryDto(reservation)).thenReturn(dto);

            assertThat(reservationService.getProReservationsToHandle(userId)).containsExactly(dto);
        }
    }

    @Nested
    class GetProConfirmedReservations {

        @Test
        void givenPrestataireUtilisateur_whenGetProConfirmedReservations_thenReturnsConfirmed() {
            UUID userId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.CONFIRMED);
            ProReservationSummaryDto dto = mock(ProReservationSummaryDto.class);
            when(reservationRepository.findByPrestataireUtilisateurIdAndStatusInOrderByStatus(
                    userId, List.of(ReservationStatus.CONFIRMED)))
                    .thenReturn(List.of(reservation));
            when(reservationMapper.toProReservationSummaryDto(reservation)).thenReturn(dto);

            assertThat(reservationService.getProConfirmedReservations(userId)).containsExactly(dto);
        }
    }

    @Nested
    class GetProBoardCounts {

        @Test
        void givenCountsByStatus_whenGetProBoardCounts_thenReturnsDtoWithThreeCounts() {
            UUID userId = UUID.randomUUID();
            when(reservationRepository.countByStatusAndPrestataireUtilisateurId(ReservationStatus.NEW, userId)).thenReturn(2);
            when(reservationRepository.countByStatusAndPrestataireUtilisateurId(ReservationStatus.IN_DISCUSSION, userId)).thenReturn(1);
            when(reservationRepository.countByStatusAndPrestataireUtilisateurId(ReservationStatus.CONFIRMED, userId)).thenReturn(3);

            ProBoardCountsDto result = reservationService.getProBoardCounts(userId);

            assertThat(result).isEqualTo(new ProBoardCountsDto(2, 1, 3));
        }
    }

    // -------------------------------------------------------------------------
    // verifyProOwnershipByReservationId
    // -------------------------------------------------------------------------

    @Nested
    class VerifyProOwnershipByReservationId {

        @Test
        void givenOwningPrestataire_whenVerifyProOwnership_thenNoException() {
            UUID userId = UUID.randomUUID();
            UUID reservationId = UUID.randomUUID();
            Utilisateur prestataireUtilisateur = Utilisateur.builder().id(userId).build();
            Prestataire prestataire = Prestataire.builder().utilisateur(prestataireUtilisateur).build();
            Reservation reservation = Reservation.builder().id(reservationId).prestataire(prestataire).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            reservationService.verifyProOwnershipByReservationId(reservationId, userId);
        }

        @Test
        void givenNonOwningUtilisateur_whenVerifyProOwnership_thenThrowsNotAllowed() {
            UUID reservationId = UUID.randomUUID();
            Utilisateur prestataireUtilisateur = Utilisateur.builder().id(UUID.randomUUID()).build();
            Prestataire prestataire = Prestataire.builder().utilisateur(prestataireUtilisateur).build();
            Reservation reservation = Reservation.builder().id(reservationId).prestataire(prestataire).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.verifyProOwnershipByReservationId(reservationId, UUID.randomUUID()))
                    .isInstanceOf(ReservationNotAllowedException.class);
        }

        @Test
        void givenUnknownReservation_whenVerifyProOwnership_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.verifyProOwnershipByReservationId(reservationId, UUID.randomUUID()))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getProReservationDetail
    // -------------------------------------------------------------------------

    @Nested
    class GetProReservationDetail {

        @Test
        void givenExistingReservation_whenGetProReservationDetail_thenReturnsMappedDtoWithLastUpdateDate() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            reservation.setEvenement(Evenement.builder().id(EVENT_ID).build());
            LocalDateTime lastUpdateDate = LocalDateTime.of(2027, 1, 10, 14, 30);
            ProReservationDetailDto dto = mock(ProReservationDetailDto.class);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.of(lastUpdateDate));
            when(reservationMapper.toProReservationDetailDto(reservation, lastUpdateDate)).thenReturn(dto);

            assertThat(reservationService.getProReservationDetail(reservationId)).isEqualTo(dto);
        }

        @Test
        void givenEventNeverModified_whenGetProReservationDetail_thenMapsWithoutLastUpdateDate() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            reservation.setEvenement(Evenement.builder().id(EVENT_ID).build());
            ProReservationDetailDto dto = mock(ProReservationDetailDto.class);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
            when(journalEvenementService.derniereModification(EVENT_ID)).thenReturn(Optional.empty());
            when(reservationMapper.toProReservationDetailDto(reservation, null)).thenReturn(dto);

            assertThat(reservationService.getProReservationDetail(reservationId)).isEqualTo(dto);
        }

        @Test
        void givenUnknownReservation_whenGetProReservationDetail_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.getProReservationDetail(reservationId))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // verifyOwnershipByReservationId
    // -------------------------------------------------------------------------

    @Nested
    class VerifyOwnershipByReservationId {

        @Test
        void givenOwningUtilisateur_whenVerifyOwnership_thenNoException() {
            UUID userId = UUID.randomUUID();
            UUID reservationId = UUID.randomUUID();
            Utilisateur owner = Utilisateur.builder().id(userId).build();
            Evenement evenement = Evenement.builder().utilisateur(owner).build();
            Reservation reservation = Reservation.builder().id(reservationId).evenement(evenement).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            reservationService.verifyOwnershipByReservationId(reservationId, userId);
        }

        @Test
        void givenNonOwningUtilisateur_whenVerifyOwnership_thenThrowsNotAllowed() {
            UUID reservationId = UUID.randomUUID();
            Utilisateur owner = Utilisateur.builder().id(UUID.randomUUID()).build();
            Evenement evenement = Evenement.builder().utilisateur(owner).build();
            Reservation reservation = Reservation.builder().id(reservationId).evenement(evenement).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.verifyOwnershipByReservationId(reservationId, UUID.randomUUID()))
                    .isInstanceOf(ReservationNotAllowedException.class);
        }

        @Test
        void givenUnknownReservation_whenVerifyOwnership_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.verifyOwnershipByReservationId(reservationId, UUID.randomUUID()))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getReservationById
    // -------------------------------------------------------------------------

    @Nested
    class GetReservationById {

        @Test
        void givenExistingReservation_whenGetReservationById_thenReturnsIt() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = reservationWith(ReservationStatus.NEW);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThat(reservationService.getReservationById(reservationId)).isEqualTo(reservation);
        }

        @Test
        void givenUnknownReservation_whenGetReservationById_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.getReservationById(reservationId))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Cas d'erreur : statuts invalides / réservation introuvable
    // -------------------------------------------------------------------------

    @Nested
    class InvalidTransitions {

        @Test
        void givenReservationConfirmed_whenCancel_thenTransitionsNormally() {
            // NEW/IN_DISCUSSION/CONFIRMED sont les seules transitions valides pour cancel ;
            // couvert par CancelTest — ici on couvre le default (statut déjà terminal)
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).status(ReservationStatus.DONE).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.cancel(reservationId, null, false))
                    .isInstanceOf(InvalidStateException.class);
        }

        @Test
        void givenReservationNotNew_whenMarkContacted_thenThrowsInvalidState() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).status(ReservationStatus.CONFIRMED).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.markContacted(reservationId))
                    .isInstanceOf(InvalidStateException.class);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        void givenUnknownReservation_whenMarkContacted_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.markContacted(reservationId))
                    .isInstanceOf(ReservationNotFoundException.class);
        }

        @Test
        void givenReservationNotInDiscussion_whenConfirm_thenThrowsInvalidState() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).status(ReservationStatus.NEW).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.confirm(reservationId))
                    .isInstanceOf(InvalidStateException.class);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        void givenUnknownReservation_whenConfirm_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.confirm(reservationId))
                    .isInstanceOf(ReservationNotFoundException.class);
        }

        @Test
        void givenReservationNotConfirmed_whenCancelByPro_thenThrowsInvalidState() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).status(ReservationStatus.NEW).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.cancelByPro(reservationId, "motif", false))
                    .isInstanceOf(InvalidStateException.class);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        void givenUnknownReservation_whenCancelByPro_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.cancelByPro(reservationId, "motif", false))
                    .isInstanceOf(ReservationNotFoundException.class);
        }

        @Test
        void givenReservationInDiscussion_whenRefuse_thenTransitionsToRefusedPostContact() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).status(ReservationStatus.IN_DISCUSSION).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            reservationService.refuse(reservationId, new RefuseReservationRequest("Plus dispo"));

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REFUSED_POST_CONTACT);
        }

        @Test
        void givenReservationNotRefusable_whenRefuse_thenThrowsInvalidState() {
            UUID reservationId = UUID.randomUUID();
            Reservation reservation = Reservation.builder().id(reservationId).status(ReservationStatus.CONFIRMED).build();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.refuse(reservationId, new RefuseReservationRequest("motif")))
                    .isInstanceOf(InvalidStateException.class);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        void givenUnknownReservation_whenRefuse_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.refuse(reservationId, new RefuseReservationRequest("motif")))
                    .isInstanceOf(ReservationNotFoundException.class);
        }

        @Test
        void givenUnknownReservation_whenCancel_thenThrowsNotFound() {
            UUID reservationId = UUID.randomUUID();
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> reservationService.cancel(reservationId, null, false))
                    .isInstanceOf(ReservationNotFoundException.class);
        }
    }

    private Reservation reservationWith(ReservationStatus status) {
        return Reservation.builder().id(UUID.randomUUID()).status(status).build();
    }

    // -------------------------------------------------------------------------
    // createConfirmed
    // -------------------------------------------------------------------------

    @Nested
    class CreateConfirmed {

        @Test
        void givenInvitedClient_whenCreateConfirmed_thenSavesConfirmedReservationAndNotifiesThePro() {
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();
            Utilisateur client = Utilisateur.builder().id(UUID.randomUUID()).build();
            Evenement evenement = Evenement.builder().id(EVENT_ID).build();
            LocalDate date = LocalDate.of(2027, 6, 12);
            when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Reservation reservation = reservationService.createConfirmed(evenement, prestataire, client, date, "lieu");

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
            assertThat(reservation.getPrestataire()).isEqualTo(prestataire);
            assertThat(reservation.getUtilisateur()).isEqualTo(client);
            assertThat(reservation.getEvenement()).isEqualTo(evenement);
            assertThat(reservation.getDate()).isEqualTo(date);
            assertThat(reservation.getRubriqueKey()).isEqualTo("lieu");
            verify(reservationEventPublisher).publishAddedBySgilt(reservation);
            verify(reservationEventPublisher, never()).publishCreated(any());
        }

        @Test
        void givenInvitedClient_whenCreateConfirmed_thenAddsTheConfirmedSystemNoteToTheFeed() {
            when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

            reservationService.createConfirmed(Evenement.builder().build(), Prestataire.builder().build(),
                    Utilisateur.builder().build(), LocalDate.of(2027, 6, 12), "lieu");

            ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
            verify(noteRepository).save(noteCaptor.capture());
            assertThat(noteCaptor.getValue().getGeneratedKey()).isEqualTo("feed.system.confirmed");
            assertThat(noteCaptor.getValue().getIsPersonal()).isFalse();
        }
    }
}
