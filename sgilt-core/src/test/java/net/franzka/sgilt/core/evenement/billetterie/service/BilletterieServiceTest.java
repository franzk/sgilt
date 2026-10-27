package net.franzka.sgilt.core.evenement.billetterie.service;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.mapper.BilletterieMapper;
import net.franzka.sgilt.core.evenement.billetterie.repository.BilletterieRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BilletterieServiceTest {

    private static final UUID EVENEMENT_ID = UUID.randomUUID();

    @Mock
    private BilletterieRepository billetterieRepository;

    @Mock
    private BilletterieMapper billetterieMapper;

    @InjectMocks
    private BilletterieService billetterieService;

    // -------------------------------------------------------------------------
    // getPublicTicketing
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicTicketing {

        @Test
        void givenBilletterie_whenGetPublicTicketing_thenMapsItWithWholeCapacityRemaining() {
            Billetterie billetterie = Billetterie.builder().status(BilletterieStatus.OPEN).unitPriceCents(1200).capacity(150).build();
            TicketingDto dto = new TicketingDto(BilletterieStatus.OPEN, 1200, 150);
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(billetterieMapper.toTicketingDto(billetterie, 150)).thenReturn(dto);

            assertThat(billetterieService.getPublicTicketing(EVENEMENT_ID)).contains(dto);
        }

        @Test
        void givenNoBilletterie_whenGetPublicTicketing_thenReturnsEmpty() {
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(billetterieService.getPublicTicketing(EVENEMENT_ID)).isEmpty();
        }
    }
}
