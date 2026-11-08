package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EBilletPdfServiceTest {

    private static final String QR_CONTENT = "SGILT:C:q3Zx9kP2mL7vT1nR8wY4sA";

    private final EBilletPdfService service = new EBilletPdfService();

    // -------------------------------------------------------------------------
    // generate
    // -------------------------------------------------------------------------

    @Nested
    class Generate {

        @Test
        void givenEBillet_whenGenerate_thenReturnsOnePagePdfWithEventDatePlaceAndValidFor() throws IOException {
            byte[] pdf = service.generate(eBillet("Le Taennel", "Scherwiller (67)", 4));

            try (PDDocument document = Loader.loadPDF(pdf)) {
                assertThat(document.getNumberOfPages()).isEqualTo(1);
                assertThat(new PDFTextStripper().getText(document))
                        .contains("Les Jeudis du Taennel")
                        .contains("Jeudi 3 décembre 2026 · 19h – 2h")
                        .contains("Le Taennel, Scherwiller (67)")
                        .contains("Valable pour 4 personnes");
            }
        }

        @Test
        void givenEBillet_whenGenerate_thenPdfEmbedsQrImage() throws IOException {
            byte[] pdf = service.generate(eBillet("Le Taennel", "Scherwiller (67)", 1));

            try (PDDocument document = Loader.loadPDF(pdf)) {
                var resources = document.getPage(0).getResources();
                boolean hasImage = false;
                for (var name : resources.getXObjectNames()) {
                    hasImage |= resources.getXObject(name) instanceof PDImageXObject;
                }
                assertThat(hasImage).isTrue();
            }
        }

        @Test
        void givenEBilletWithoutVenue_whenGenerate_thenPlaceIsCityAlone() throws IOException {
            byte[] pdf = service.generate(eBillet(null, "Scherwiller (67)", 1));

            try (PDDocument document = Loader.loadPDF(pdf)) {
                assertThat(new PDFTextStripper().getText(document))
                        .contains("Scherwiller (67)")
                        .doesNotContain(", Scherwiller");
            }
        }

        @Test
        void givenTitleWithHtmlSpecialChars_whenGenerate_thenTitleIsRenderedVerbatim() throws IOException {
            EBilletDto eBillet = new EBilletDto("Rock & <Roll>", null, null, infos("Le Taennel", "Scherwiller (67)"),
                    1, QR_CONTENT, QrCodeUtils.pngDataUri(QR_CONTENT), true);

            byte[] pdf = service.generate(eBillet);

            try (PDDocument document = Loader.loadPDF(pdf)) {
                assertThat(new PDFTextStripper().getText(document)).contains("Rock & <Roll>");
            }
        }
    }

    private EBilletDto eBillet(String venue, String city, int quantity) {
        return new EBilletDto("Les Jeudis du Taennel", "bank/taennel.png", "evenement_public", infos(venue, city),
                quantity, QR_CONTENT, QrCodeUtils.pngDataUri(QR_CONTENT), true);
    }

    private EvenementInfosDto infos(String venue, String city) {
        return new EvenementInfosDto("Édition du 3 décembre", "Concert et apéro", LocalDate.of(2026, 12, 3),
                LocalDateTime.of(2026, 12, 3, 19, 0), LocalDateTime.of(2026, 12, 4, 2, 0),
                venue, city, List.of("Vestiaire"));
    }
}
