package net.franzka.sgilt.core.evenement.billetterie.commande.mapper;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.PracticalInfoDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommandeMapperTest {

    private final CommandeMapper mapper = new CommandeMapperImpl();

    // -------------------------------------------------------------------------
    // toCommande
    // -------------------------------------------------------------------------

    @Nested
    class ToCommande {

        @Test
        void givenRequestAndBilletterie_whenToCommande_thenMapsPendingCommandeAtBilletteriePrice() {
            Billetterie billetterie = Billetterie.builder().unitPriceCents(1200).build();
            CreateCommandeRequest request =
                    new CreateCommandeRequest("slug", 3, "Jeanne", "Martin", "jeanne@example.com");
            LocalDateTime expiresAt = LocalDateTime.of(2026, 3, 12, 19, 15);

            Commande commande = mapper.toCommande(request, billetterie, expiresAt);

            assertThat(commande.getId()).isNull();
            assertThat(commande.getCreatedAt()).isNull();
            assertThat(commande.getBilletterie()).isSameAs(billetterie);
            assertThat(commande.getQuantity()).isEqualTo(3);
            assertThat(commande.getUnitPriceCents()).isEqualTo(1200);
            assertThat(commande.getBuyerFirstName()).isEqualTo("Jeanne");
            assertThat(commande.getBuyerLastName()).isEqualTo("Martin");
            assertThat(commande.getBuyerEmail()).isEqualTo("jeanne@example.com");
            assertThat(commande.getStatus()).isEqualTo(CommandeStatus.PENDING);
            assertThat(commande.getExpiresAt()).isEqualTo(expiresAt);
        }
    }

    // -------------------------------------------------------------------------
    // toCommandeDto
    // -------------------------------------------------------------------------

    @Nested
    class ToCommandeDto {

        @Test
        void givenCommandeAndClientSecret_whenToCommandeDto_thenMapsItWithTotal() {
            UUID id = UUID.randomUUID();
            LocalDateTime expiresAt = LocalDateTime.of(2026, 3, 12, 19, 15);
            Commande commande = Commande.builder()
                    .id(id).quantity(3).unitPriceCents(1200).expiresAt(expiresAt).build();

            assertThat(mapper.toCommandeDto(commande, "pi_123_secret_456"))
                    .isEqualTo(new CommandeDto(id, 3, 3600, expiresAt, "pi_123_secret_456"));
        }
    }

    // -------------------------------------------------------------------------
    // toCommandeSummaryDto
    // -------------------------------------------------------------------------

    @Nested
    class ToCommandeSummaryDto {

        @Test
        void givenCommande_whenToCommandeSummaryDto_thenMapsItWithStatusAndTotal() {
            UUID id = UUID.randomUUID();
            Commande commande = Commande.builder()
                    .id(id).status(CommandeStatus.PAID).quantity(3).unitPriceCents(1200).build();

            assertThat(mapper.toCommandeSummaryDto(commande))
                    .isEqualTo(new CommandeSummaryDto(id, CommandeStatus.PAID, 3, 1200, 3600));
        }
    }

    // -------------------------------------------------------------------------
    // toEBilletDto
    // -------------------------------------------------------------------------

    @Nested
    class ToEBilletDto {

        @Test
        void givenPaidCommandeAndPracticalInfo_whenToEBilletDto_thenMapsEventQuantityAndQrContent() {
            Evenement evenement = Evenement.builder()
                    .title("Les Jeudis du Taennel").imagePath("bank/taennel.png").eventType("evenement_public").build();
            Billetterie billetterie = Billetterie.builder().evenement(evenement).build();
            Commande commande = Commande.builder()
                    .billetterie(billetterie).quantity(4).status(CommandeStatus.PAID).code("q3Zx9kP2mL7vT1nR8wY4sA").build();
            PracticalInfoDto practicalInfo = new PracticalInfoDto(LocalDate.of(2026, 12, 3),
                    LocalDateTime.of(2026, 12, 3, 19, 0), null, "Le Taennel", "Scherwiller (67)", List.of("Vestiaire"));

            assertThat(mapper.toEBilletDto(commande, practicalInfo))
                    .isEqualTo(new EBilletDto("Les Jeudis du Taennel", "bank/taennel.png", "evenement_public",
                            practicalInfo, 4, "SGILT:C:q3Zx9kP2mL7vT1nR8wY4sA"));
        }
    }
}
