package net.franzka.sgilt.core.evenement.page.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;

import java.util.List;

/**
 * Contenu de la page d'un événement, tel que le saisit son organisateur.
 *
 * @param slug          le slug de la page (/e/{slug}), unique
 * @param tag           le libellé libre au-dessus du titre, ou null
 * @param subtitle      le sous-titre, ou null
 * @param sectionTitle  le titre de la présentation, ou null
 * @param description   le texte de présentation, ou null
 * @param importantInfo les infos importantes, montrées avant l'achat (null = aucune)
 * @param practicalInfo les infos pratiques, montrées après l'achat (null = aucune)
 * @param pictos        les points forts de la page (null = aucun)
 * @param localisation  le lien de partage Google Maps, l'adresse ou le point GPS du lieu, ou null
 */
public record PageContentDto(
        // En kebab-case : minuscules, chiffres, tirets simples (ex. les-jeudis-du-taennel).
        @NotBlank @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$") String slug,
        String tag,
        String subtitle,
        String sectionTitle,
        String description,
        List<String> importantInfo,
        List<String> practicalInfo,
        List<PageEvenementPicto> pictos,
        @Size(max = 255) String localisation
) {}
