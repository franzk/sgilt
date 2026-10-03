package net.franzka.sgilt.core.template.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.prestataire.exception.SousCategorieNotFoundException;
import net.franzka.sgilt.core.prestataire.service.CategorieService;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.template.domain.Template;
import net.franzka.sgilt.core.template.domain.TemplateDocument;
import net.franzka.sgilt.core.template.domain.TemplateRubrique;
import net.franzka.sgilt.core.template.repository.TemplateDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service des templates d'événement : toute règle qui dépend du template (rubriques d'un type
 * d'événement, rubrique d'une sous-catégorie…) passe par ici. Un type sans template n'a pas de
 * rubriques. La rubrique {@value #RUBRIQUE_AUTRE} n'est jamais dans un template : elle reçoit ce
 * qu'aucune rubrique ne regroupe, et n'existe dans un événement que si une réservation y tombe.
 */
@Service
@RequiredArgsConstructor
public class TemplateService {

    /** Rubrique de ce qu'aucune rubrique du template ne regroupe. */
    public static final String RUBRIQUE_AUTRE = "autre";

    private final TemplateDocumentRepository templateDocumentRepository;
    private final CategorieService categorieService;
    private final PrestataireService prestataireService;

    /**
     * Retourne les rubriques d'un nouvel événement du type donné : celles du template, vides, dans
     * l'ordre d'affichage.
     *
     * @param eventType le type d'événement ('mariage'…)
     * @return les rubriques, vide si le type n'a pas de template
     */
    public List<RubriqueDto> getNewEventRubriques(String eventType) {
        return findTemplate(eventType).map(Template::rubriques).orElse(List.of()).stream()
                .map(rubrique -> new RubriqueDto(rubrique.key(), List.of()))
                .toList();
    }

    /**
     * Construit les rubriques d'un événement né d'une demande unique : celles du template de son
     * type, plus {@value #RUBRIQUE_AUTRE} en fin si aucune ne regroupe la demande, la demande
     * rangée dans sa rubrique.
     *
     * @param eventType le type d'événement
     * @param demande   la demande initiée (le prestataire visé donne sa sous-catégorie)
     * @return les rubriques de l'événement, dans l'ordre d'affichage
     * @throws SousCategorieNotFoundException si la sous-catégorie du prestataire n'existe pas
     */
    public List<RubriqueDto> rubriquesWithDemande(String eventType, DemandeInitieeDto demande) {
        String subcatKey = prestataireService.getById(demande.prestataireId()).getSubcatKey();
        List<TemplateRubrique> rubriques = findTemplate(eventType).map(Template::rubriques).orElse(List.of());
        String demandeRubriqueKey = rubriqueKeyOf(rubriques, subcatKey);
        List<String> keys = new ArrayList<>(rubriques.stream().map(TemplateRubrique::key).toList());
        if (demandeRubriqueKey.equals(RUBRIQUE_AUTRE)) keys.add(RUBRIQUE_AUTRE);
        return keys.stream()
                .map(key -> new RubriqueDto(key,
                        key.equals(demandeRubriqueKey) ? List.of(demande) : List.of()))
                .toList();
    }

    // Rubrique d'une sous-catégorie parmi celles du template : celle qui regroupe la sous-catégorie,
    // sinon celle qui regroupe sa catégorie entière, sinon RUBRIQUE_AUTRE.
    private String rubriqueKeyOf(List<TemplateRubrique> rubriques, String subcatKey) {
        String categoryKey = categorieService.getCategoryKeyOf(subcatKey);
        return rubriques.stream()
                .filter(rubrique -> rubrique.subcategories().contains(subcatKey))
                .findFirst()
                .or(
                        () -> rubriques.stream()
                                .filter(rubrique -> rubrique.categories().contains(categoryKey))
                                .findFirst()
                )
                .map(TemplateRubrique::key)
                .orElse(RUBRIQUE_AUTRE);
    }

    private Optional<Template> findTemplate(String eventType) {
        return templateDocumentRepository.findById(TemplateDocument.SINGLE_ID)
                .map(TemplateDocument::getContent)
                .orElse(List.of())
                .stream()
                .filter(template -> template.type().equals(eventType))
                .findFirst();
    }
}
