package net.franzka.sgilt.core.evenement.page.mapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapsUrlUtilsTest {

    // -------------------------------------------------------------------------
    // mapsUrl
    // -------------------------------------------------------------------------

    @Nested
    class MapsUrl {

        @Test
        void givenNullOrBlank_whenMapsUrl_thenReturnsNull() {
            assertThat(MapsUrlUtils.mapsUrl(null)).isNull();
            assertThat(MapsUrlUtils.mapsUrl("   ")).isNull();
        }

        @Test
        void givenShareLink_whenMapsUrl_thenReturnsItAsIs() {
            assertThat(MapsUrlUtils.mapsUrl(" https://maps.app.goo.gl/AbCd1234 "))
                    .isEqualTo("https://maps.app.goo.gl/AbCd1234");
        }

        @Test
        void givenAddress_whenMapsUrl_thenReturnsEncodedSearch() {
            assertThat(MapsUrlUtils.mapsUrl("12 rue des Juifs, Strasbourg"))
                    .isEqualTo("https://www.google.com/maps/search/?api=1&query=12+rue+des+Juifs%2C+Strasbourg");
        }

        @Test
        void givenGpsPoint_whenMapsUrl_thenReturnsSearch() {
            assertThat(MapsUrlUtils.mapsUrl("48.5839,7.7455"))
                    .isEqualTo("https://www.google.com/maps/search/?api=1&query=48.5839%2C7.7455");
        }

        @Test
        void givenJavascriptScheme_whenMapsUrl_thenReturnsSearchNotTheValue() {
            assertThat(MapsUrlUtils.mapsUrl("javascript:alert(1)"))
                    .startsWith("https://www.google.com/maps/search/?api=1&query=");
        }
    }
}
