package net.franzka.sgilt.core.evenement.page.dto;

import jakarta.validation.constraints.NotBlank;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;

import java.util.List;

/**
 * Page d'un événement telle que la saisit le back-office admin.
 *
 * @param slug          le slug de la page (/e/{slug}), unique
 * @param tag           le libellé libre au-dessus du titre, ou null
 * @param subtitle      le sous-titre, ou null
 * @param sectionTitle  le titre de la présentation, ou null
 * @param description   le texte de présentation, ou null
 * @param importantInfo les infos importantes, montrées avant l'achat (null = aucune)
 * @param practicalInfo les infos pratiques, montrées après l'achat (null = aucune)
 * @param pictos        les points forts de la page (null = aucun)
 */
public record AdminPageDto(
        @NotBlank String slug,
        String tag,
        String subtitle,
        String sectionTitle,
        String description,
        List<String> importantInfo,
        List<String> practicalInfo,
        List<PageEvenementPicto> pictos
) {}
