package net.franzka.sgilt.core.evenement.billetterie.commande.mapper;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
        void givenCommande_whenToCommandeDto_thenMapsItWithTotal() {
            UUID id = UUID.randomUUID();
            LocalDateTime expiresAt = LocalDateTime.of(2026, 3, 12, 19, 15);
            Commande commande = Commande.builder()
                    .id(id).quantity(3).unitPriceCents(1200).expiresAt(expiresAt).build();

            assertThat(mapper.toCommandeDto(commande)).isEqualTo(new CommandeDto(id, 3, 3600, expiresAt));
        }
    }
}
