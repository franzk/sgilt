package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class EBilletPdfServiceTest {

    private static final String QR_CONTENT = "SGILT:C:q3Zx9kP2mL7vT1nR8wY4sA";

    private final EBilletPdfService service = new EBilletPdfService();

    // -------------------------------------------------------------------------
    // generate
    // -------------------------------------------------------------------------

    @Nested
    class Generate {

        @Test
        void givenEBillet_whenGenerate_thenReturnsOneA4PortraitPage() throws IOException {
            try (PDDocument document = Loader.loadPDF(service.generate(eBillet(infos("Le Taennel", "Scherwiller (67)"), 4)))) {
                assertThat(document.getNumberOfPages()).isEqualTo(1);
                PDRectangle page = document.getPage(0).getMediaBox();
                assertThat(page.getWidth()).isCloseTo(PDRectangle.A4.getWidth(), offset(1f));
                assertThat(page.getHeight()).isCloseTo(PDRectangle.A4.getHeight(), offset(1f));
            }
        }

        @Test
        void givenEBillet_whenGenerate_thenShowsSameContentAsEBilletPage() throws IOException {
            String text = text(service.generate(eBillet(infos("Le Taennel", "Scherwiller (67)"), 4)));

            assertThat(text)
                    .contains("ÉDITION DU 3 DÉCEMBRE")
                    .contains("Les Jeudis du Taennel")
                    .contains("Concert et apéro")
                    .contains("Valable pour 4 personnes")
                    .contains("À présenter à l'entrée")
                    .contains("Informations pratiques")
                    .contains("Jeudi 3 décembre 2026 · 19h – 2h")
                    .contains("Le Taennel · Scherwiller (67)")
                    .contains("Vestiaire")
                    .contains("Parking gratuit")
                    .doesNotContain("contact@sgilt.alsace");
        }

        @Test
        void givenEBillet_whenGenerate_thenEmbedsLogoAndQrImages() throws IOException {
            try (PDDocument document = Loader.loadPDF(service.generate(eBillet(infos("Le Taennel", "Scherwiller (67)"), 1)))) {
                PDResources resources = document.getPage(0).getResources();
                int images = 0;
                for (COSName name : resources.getXObjectNames()) {
                    images += resources.getXObject(name) instanceof PDImageXObject ? 1 : 0;
                }
                assertThat(images).isEqualTo(2);
            }
        }

        @Test
        void givenEBillet_whenGenerate_thenEmbedsCharterFonts() throws IOException {
            try (PDDocument document = Loader.loadPDF(service.generate(eBillet(infos("Le Taennel", "Scherwiller (67)"), 1)))) {
                PDResources resources = document.getPage(0).getResources();
                List<String> fonts = new ArrayList<>();
                for (COSName name : resources.getFontNames()) {
                    fonts.add(resources.getFont(name).getName());
                }
                // Polices embarquées en sous-ensemble : nom préfixé d'un tag (« ABCDEF+Inter-Regular »).
                assertThat(fonts).anyMatch(font -> font.contains("Inter-Regular"))
                        .anyMatch(font -> font.contains("Inter-SemiBold"))
                        .anyMatch(font -> font.contains("CormorantGaramond"));
            }
        }

        @Test
        void givenEBilletWithoutVenue_whenGenerate_thenPlaceIsCityAlone() throws IOException {
            String text = text(service.generate(eBillet(infos(null, "Scherwiller (67)"), 1)));

            assertThat(text).contains("Scherwiller (67)").doesNotContain("· Scherwiller");
        }

        @Test
        void givenEBilletWithoutTagSubtitleNorPracticalInfo_whenGenerate_thenOmitsThem() throws IOException {
            EvenementInfosDto infos = new EvenementInfosDto(null, null, LocalDate.of(2026, 12, 3),
                    LocalDateTime.of(2026, 12, 3, 19, 0), null, "Le Taennel", "Scherwiller (67)", null, List.of());

            String text = text(service.generate(eBillet(infos, 1)));

            assertThat(text).contains("Les Jeudis du Taennel").doesNotContain("null");
        }

        @Test
        void givenTextsWithHtmlSpecialChars_whenGenerate_thenRendersThemVerbatim() throws IOException {
            EvenementInfosDto infos = new EvenementInfosDto("<b>Tag</b>", "Soirée \"spéciale\"", LocalDate.of(2026, 12, 3),
                    null, null, "Le Taennel", "Scherwiller (67)", null, List.of("Bière & <vin>"));
            EBilletDto eBillet = new EBilletDto("Rock & <Roll>", null, null, infos,
                    1, QrCodeUtils.pngDataUri(QR_CONTENT), true);

            String text = text(service.generate(eBillet));

            assertThat(text)
                    .contains("Rock & <Roll>")
                    .contains("<B>TAG</B>")
                    .contains("Soirée \"spéciale\"")
                    .contains("Bière & <vin>");
        }
    }

    private String text(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private EBilletDto eBillet(EvenementInfosDto infos, int quantity) {
        return new EBilletDto("Les Jeudis du Taennel", "bank/taennel.png", "evenement_public", infos,
                quantity, QrCodeUtils.pngDataUri(QR_CONTENT), true);
    }

    private EvenementInfosDto infos(String venue, String city) {
        return new EvenementInfosDto("Édition du 3 décembre", "Concert et apéro", LocalDate.of(2026, 12, 3),
                LocalDateTime.of(2026, 12, 3, 19, 0), LocalDateTime.of(2026, 12, 4, 2, 0),
                venue, city, null, List.of("Vestiaire", "Parking gratuit"));
    }
}
