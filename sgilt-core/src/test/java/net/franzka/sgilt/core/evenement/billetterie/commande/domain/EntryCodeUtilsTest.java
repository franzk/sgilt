package net.franzka.sgilt.core.evenement.billetterie.commande.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntryCodeUtilsTest {

    @Nested
    class ParseCommandeCode {

        @Test
        void givenCommandeQrContent_whenParseCommandeCode_thenReturnsItsCode() {
            String code = EntryCodeUtils.newCode();

            assertThat(EntryCodeUtils.parseCommandeCode(EntryCodeUtils.commandeQrContent(code))).contains(code);
        }

        @Test
        void givenOtherQrContent_whenParseCommandeCode_thenReturnsEmpty() {
            assertThat(EntryCodeUtils.parseCommandeCode("https://example.com")).isEmpty();
        }

        @Test
        void givenPrefixWithoutCode_whenParseCommandeCode_thenReturnsEmpty() {
            assertThat(EntryCodeUtils.parseCommandeCode("SGILT:C:")).isEmpty();
        }

        @Test
        void givenNull_whenParseCommandeCode_thenReturnsEmpty() {
            assertThat(EntryCodeUtils.parseCommandeCode(null)).isEmpty();
        }
    }
}
