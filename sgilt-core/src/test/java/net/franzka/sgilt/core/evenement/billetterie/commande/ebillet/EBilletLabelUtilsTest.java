package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EBilletLabelUtilsTest {

    // -------------------------------------------------------------------------
    // validFor
    // -------------------------------------------------------------------------

    @Nested
    class ValidFor {

        @Test
        void givenOnePlace_whenValidFor_thenReturnsSingular() {
            assertThat(EBilletLabelUtils.validFor(1)).isEqualTo("1 personne");
        }

        @Test
        void givenSeveralPlaces_whenValidFor_thenReturnsPlural() {
            assertThat(EBilletLabelUtils.validFor(4)).isEqualTo("4 personnes");
        }
    }
}
