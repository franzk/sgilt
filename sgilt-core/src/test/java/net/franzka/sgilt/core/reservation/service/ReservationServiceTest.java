package net.franzka.sgilt.core.reservation.service;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private ReservationRepository reservationRepository;


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

    private Reservation reservationWith(ReservationStatus status) {
        return Reservation.builder().id(UUID.randomUUID()).status(status).build();
    }
}
