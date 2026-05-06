package net.franzka.sgilt.core.evenement.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.mapper.EvenementMapper;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvenementServiceTest {

    private static final UUID USER_ID  = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock private EvenementRepository evenementRepository;
    @Mock private ReservationService   reservationService;
    @Mock private EvenementMapper      evenementMapper;

    @InjectMocks
    private EvenementService evenementService;

    // -------------------------------------------------------------------------
    // getUserEvents
    // -------------------------------------------------------------------------

    @Nested
    class GetUserEvents {

        @Test
        void givenUserWithOneEvent_whenGetUserEvents_thenReturnsMappedDto() {
            Evenement           event  = Evenement.builder().id(EVENT_ID).name("Mariage").date(LocalDate.now()).build();
            ReservationCounts   counts = new ReservationCounts(1, 0);
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
            Evenement   event1  = Evenement.builder().id(EVENT_ID).name("E1").date(LocalDate.now()).build();
            Evenement   event2  = Evenement.builder().id(eventId2).name("E2").date(LocalDate.now()).build();
            ReservationCounts counts1 = new ReservationCounts(2, 0);
            ReservationCounts counts2 = new ReservationCounts(0, 1);
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
}
