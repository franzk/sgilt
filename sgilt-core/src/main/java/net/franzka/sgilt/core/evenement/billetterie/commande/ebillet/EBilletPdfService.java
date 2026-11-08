package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.EBilletPdfException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mailer.EventDateLabelUtils;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Génère le PDF d'un e-billet : le gabarit HTML {@value #TEMPLATE_PATH}, rempli avec l'e-billet,
 * rendu en PDF A4 par openhtmltopdf, aux polices et au logo de la charte Sgilt. Généré à la volée
 * à chaque demande, jamais stocké : il reflète toujours l'état actuel de l'événement (horaires, lieu).
 */
@Service
@Slf4j
public class EBilletPdfService {

    private static final String TEMPLATE_PATH = "ebillet/e-billet.html";
    private static final String LOGO_PATH = "ebillet/sgilt-logo.png";
    private static final String PNG_DATA_URI_PREFIX = "data:image/png;base64,";
    private static final String FONTS_PATH = "ebillet/fonts/";
    private static final String INTER_FAMILY = "Inter";
    private static final String CORMORANT_GARAMOND_FAMILY = "Cormorant Garamond";
    // Polices embarquées : famille et graisse telles que référencées par le CSS du gabarit.
    private static final List<EmbeddedFont> EMBEDDED_FONTS = List.of(
            new EmbeddedFont("Inter-Regular.ttf", INTER_FAMILY, 400),
            new EmbeddedFont("Inter-SemiBold.ttf", INTER_FAMILY, 600),
            new EmbeddedFont("CormorantGaramond-Bold.ttf", CORMORANT_GARAMOND_FAMILY, 700));
    // Même séparateur que la page e-billet entre le lieu et la ville.
    private static final String PLACE_SEPARATOR = " · ";

    private final String template;
    private final String logoDataUri;

    /**
     * Construit le service en chargeant une fois pour toutes le gabarit et le logo.
     *
     * @throws UncheckedIOException si le gabarit ou le logo sont introuvables
     */
    public EBilletPdfService() {
        this.template = new String(readResource(TEMPLATE_PATH), StandardCharsets.UTF_8);
        this.logoDataUri = PNG_DATA_URI_PREFIX + Base64.getEncoder().encodeToString(readResource(LOGO_PATH));
    }

    /**
     * Génère le PDF d'un e-billet.
     *
     * @param eBillet l'e-billet, avec son QR déjà dessiné
     * @return le contenu du PDF
     * @throws EBilletPdfException si le moteur PDF échoue
     */
    public byte[] generate(EBilletDto eBillet) {
        String html = render(eBillet);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            for (EmbeddedFont font : EMBEDDED_FONTS) {
                // subset = true : seuls les caractères utilisés sont embarqués, le PDF reste léger.
                builder.useFont(() -> openFont(font), font.family(), font.weight(), FontStyle.NORMAL, true);
            }
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            return output.toByteArray();
        } catch (IOException | UncheckedIOException e) {
            throw new EBilletPdfException(e);
        }
    }

    // Remplace chaque {variable} du gabarit. Les blocs facultatifs sont du HTML déjà construit (valeurs
    // échappées) ; tout le reste est échappé ici.
    private String render(EBilletDto eBillet) {
        EvenementInfosDto infos = eBillet.evenementInfos();
        Map<String, String> escapedVariables = Map.of(
                "title", eBillet.title(),
                "date", EventDateLabelUtils.eventDateLabel(infos.date(), infos.startsAt(), infos.endsAt()),
                "place", placeLabel(infos),
                "validFor", EBilletLabelUtils.validFor(eBillet.quantity()),
                "qrImage", eBillet.qrImage(),
                "logo", logoDataUri);
        Map<String, String> htmlBlocks = Map.of(
                "tagBlock", paragraphOrEmpty("tag", infos.tag()),
                "subtitleBlock", paragraphOrEmpty("subtitle", infos.subtitle()),
                "practicalInfoBlock", practicalInfoList(infos.practicalInfo()));
        String html = template;
        for (Map.Entry<String, String> variable : escapedVariables.entrySet()) {
            html = html.replace(placeholder(variable.getKey()), escape(variable.getValue()));
        }
        for (Map.Entry<String, String> block : htmlBlocks.entrySet()) {
            html = html.replace(placeholder(block.getKey()), block.getValue());
        }
        return html;
    }

    private String placeholder(String name) {
        return "{" + name + "}";
    }

    // Encodage UTF-8 : n'échappe que les caractères spéciaux HTML, pas les accents (les entités
    // nommées comme &eacute; sont inconnues du parseur XML du gabarit).
    private String escape(String value) {
        return HtmlUtils.htmlEscape(value, StandardCharsets.UTF_8.name());
    }

    // Tag et sous-titre sont facultatifs : pas de paragraphe vide qui décalerait la mise en page.
    private String paragraphOrEmpty(String cssClass, String text) {
        return isBlank(text) ? "" : "<p class=\"" + cssClass + "\">" + escape(text) + "</p>";
    }

    private String practicalInfoList(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        return lines.stream()
                .map(line -> "<li>" + escape(line) + "</li>")
                .collect(Collectors.joining("", "<ul>", "</ul>"));
    }

    // « Le Taennel · Scherwiller (67) » ; lieu et ville sont facultatifs sur l'événement.
    private String placeLabel(EvenementInfosDto infos) {
        return Stream.of(infos.venue(), infos.city())
                .filter(part -> !isBlank(part))
                .collect(Collectors.joining(PLACE_SEPARATOR));
    }

    private boolean isBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }

    // Ouvert par openhtmltopdf au moment du rendu, qui le referme ; son fournisseur ne lève pas d'IOException.
    private InputStream openFont(EmbeddedFont font) {
        try {
            return new ClassPathResource(FONTS_PATH + font.fileName()).getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException("Police de l'e-billet introuvable : " + font.fileName(), e);
        }
    }

    private byte[] readResource(String path) {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return input.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Ressource de l'e-billet introuvable : " + path, e);
        }
    }

    // Une police embarquée dans le PDF : son fichier sous FONTS_PATH, sa famille et sa graisse CSS.
    private record EmbeddedFont(String fileName, String family, int weight) {
    }
}
