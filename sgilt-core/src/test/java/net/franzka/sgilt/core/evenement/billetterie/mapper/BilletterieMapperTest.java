package net.franzka.sgilt.core.evenement.billetterie.mapper;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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

    // -------------------------------------------------------------------------
    // toAdminTicketingDto
    // -------------------------------------------------------------------------

    @Nested
    class ToAdminTicketingDto {

        @Test
        void givenBilletterie_whenToAdminTicketingDto_thenMapsAllFields() {
            Billetterie billetterie = Billetterie.builder()
                    .status(BilletterieStatus.NOT_OPEN).unitPriceCents(1200).capacity(150).build();

            assertThat(mapper.toAdminTicketingDto(billetterie))
                    .isEqualTo(new AdminTicketingDto(BilletterieStatus.NOT_OPEN, 1200, 150));
        }
    }

    // -------------------------------------------------------------------------
    // toBilletterie
    // -------------------------------------------------------------------------

    @Nested
    class ToBilletterie {

        @Test
        void givenTicketingAndEvenement_whenToBilletterie_thenMapsAllFields() {
            Evenement evenement = Evenement.builder().id(UUID.randomUUID()).build();

            Billetterie billetterie = mapper.toBilletterie(
                    new AdminTicketingDto(BilletterieStatus.OPEN, 1200, 150), evenement);

            assertThat(billetterie.getId()).isNull();
            assertThat(billetterie.getEvenement()).isSameAs(evenement);
            assertThat(billetterie.getStatus()).isEqualTo(BilletterieStatus.OPEN);
            assertThat(billetterie.getUnitPriceCents()).isEqualTo(1200);
            assertThat(billetterie.getCapacity()).isEqualTo(150);
        }
    }

}
