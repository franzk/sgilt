package net.franzka.sgilt.core.evenement.billetterie.mapper;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BilletterieMapperTest {

    private final BilletterieMapper mapper = new BilletterieMapperImpl();

    // -------------------------------------------------------------------------
    // toTicketingDto
    // -------------------------------------------------------------------------

    @Nested
    class ToTicketingDto {

        @Test
        void givenBilletterieAndRemainingTickets_whenToTicketingDto_thenMapsAllFields() {
            Billetterie billetterie = Billetterie.builder()
                    .status(BilletterieStatus.OPEN).unitPriceCents(1200).capacity(150).build();

            TicketingDto dto = mapper.toTicketingDto(billetterie, 7);

            assertThat(dto).isEqualTo(new TicketingDto(BilletterieStatus.OPEN, 1200, 7));
        }
    }
}
