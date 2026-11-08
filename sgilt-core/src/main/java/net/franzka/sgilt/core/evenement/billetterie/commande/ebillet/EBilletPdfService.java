package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Génère le PDF d'un e-billet : le gabarit HTML {@value #TEMPLATE_PATH}, rempli avec l'e-billet,
 * rendu en PDF par openhtmltopdf. Généré à la volée à chaque demande, jamais stocké : il reflète
 * toujours l'état actuel de l'événement (horaires, lieu).
 */
@Service
@Slf4j
public class EBilletPdfService {

    private static final String TEMPLATE_PATH = "ebillet/e-billet.html";
    private static final String PLACE_SEPARATOR = ", ";

    private final String template;

    /**
     * Construit le service en chargeant le gabarit une fois pour toutes.
     *
     * @throws UncheckedIOException si le gabarit est introuvable
     */
    public EBilletPdfService() {
        this.template = loadTemplate();
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
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            return output.toByteArray();
        } catch (IOException e) {
            throw new EBilletPdfException(e);
        }
    }

    // Remplace chaque {variable} du gabarit, valeurs échappées : le gabarit est du XHTML, parsé en XML.
    private String render(EBilletDto eBillet) {
        EvenementInfosDto infos = eBillet.evenementInfos();
        Map<String, String> variables = Map.of(
                "title", eBillet.title(),
                "date", EventDateLabelUtils.eventDateLabel(infos.date(), infos.startsAt(), infos.endsAt()),
                "place", placeLabel(infos),
                "validFor", EBilletLabelUtils.validFor(eBillet.quantity()),
                "qrImage", eBillet.qrImage());
        String html = template;
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            // Encodage UTF-8 : n'échappe que les caractères spéciaux HTML, pas les accents (entités
            // nommées comme &eacute; inconnues du parseur XML).
            html = html.replace("{" + variable.getKey() + "}", HtmlUtils.htmlEscape(variable.getValue(), "UTF-8"));
        }
        return html;
    }

    // « Le Taennel, Scherwiller (67) » ; lieu et ville sont facultatifs sur l'événement.
    private String placeLabel(EvenementInfosDto infos) {
        return Stream.of(infos.venue(), infos.city())
                .filter(Objects::nonNull)
                .filter(part -> !part.isBlank())
                .collect(Collectors.joining(PLACE_SEPARATOR));
    }

    private String loadTemplate() {
        try (InputStream input = new ClassPathResource(TEMPLATE_PATH).getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Gabarit de l'e-billet introuvable : " + TEMPLATE_PATH, e);
        }
    }
}
