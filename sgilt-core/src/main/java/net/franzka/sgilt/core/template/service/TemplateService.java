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

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public List<RubriqueDto> getEventRubriqueFromDemande(String eventType, DemandeInitieeDto demande) {
        List<String> templateKeys = findTemplate(eventType).map(Template::rubriques).orElse(List.of()).stream()
                .map(TemplateRubrique::key)
                .toList();
        return placeInRubriques(eventType, templateKeys, List.of(demande),
                        d -> prestataireService.getById(d.prestataireId()).getSubcatKey())
                .entrySet().stream()
                .map(rubrique -> new RubriqueDto(rubrique.getKey(), rubrique.getValue()))
                .toList();
    }

    /**
     * Range des éléments dans les rubriques d'un événement. Chaque élément va dans la rubrique de
     * sa sous-catégorie : celle du template qui regroupe la sous-catégorie, sinon celle qui
     * regroupe sa catégorie entière, sinon {@value #RUBRIQUE_AUTRE}. Les rubriques données gardent
     * leur ordre (vides comprises) ; celles qui reçoivent un élément sans y être sont ajoutées en
     * fin, {@value #RUBRIQUE_AUTRE} en dernier.
     *
     * @param eventType    le type d'événement (son template donne la règle de rangement)
     * @param rubriqueKeys les rubriques de départ, dans l'ordre d'affichage
     * @param items        les éléments à ranger (demandes initiées, réservations…)
     * @param subcatOf     donne la sous-catégorie d'un élément
     * @param <T>          le type des éléments
     * @return chaque rubrique, dans l'ordre d'affichage, avec ses éléments
     * @throws SousCategorieNotFoundException si la sous-catégorie d'un élément n'existe pas
     */
    public <T> Map<String, List<T>> placeInRubriques(String eventType, List<String> rubriqueKeys,
                                                     List<T> items, Function<T, String> subcatOf) {
        // 1 — on récupère les rubriques du template du type d'événement : pas pour savoir quelles
        // rubriques rendre (ce sont rubriqueKeys), mais pour la règle de rangement — c'est le
        // template qui dit quelle rubrique regroupe quelles catégories et sous-catégories.
        List<TemplateRubrique> templateRubriques = findTemplate(eventType).map(Template::rubriques).orElse(List.of());

        // 2 — on regroupe les éléments par rubrique : pour chaque élément, sa sous-catégorie
        // (subcatOf) donne sa rubrique (rubriqueKeyOf).
        Map<String, List<T>> itemsByRubrique = items.stream()
                .collect(Collectors.groupingBy(
                        item -> rubriqueKeyOf(templateRubriques, subcatOf.apply(item)),  // 1. la clé de groupe : la rubrique de l'élément
                        LinkedHashMap::new,                                              // 2. la Map à créer pour stocker les groupes
                        Collectors.toList()));                                           // 3. ce qu'on met dans chaque groupe : une liste

        // 3 — d'abord les rubriques de départ, dans leur ordre, chacune avec ses éléments
        // (liste vide si aucun élément n'y tombe : une rubrique de départ est rendue même vide).
        Map<String, List<T>> placed = new LinkedHashMap<>();
        rubriqueKeys.forEach(key -> placed.put(key, itemsByRubrique.getOrDefault(key, List.of())));

        // Étape 4 — puis, en fin, les rubriques qui ont reçu des éléments sans faire partie des
        // rubriques de départ. Le tri compare « est-ce RUBRIQUE_AUTRE ? » (false avant true) :
        // RUBRIQUE_AUTRE passe en dernier, les autres gardent leur ordre d'arrivée (tri stable).
        itemsByRubrique.keySet().stream()
                .filter(key -> !placed.containsKey(key))
                .sorted(Comparator.comparing(RUBRIQUE_AUTRE::equals))
                .forEach(key -> placed.put(key, itemsByRubrique.get(key)));
        return placed;
    }

    // Rubrique d'une sous-catégorie parmi les rubriques du template (règle de placeInRubriques).
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
