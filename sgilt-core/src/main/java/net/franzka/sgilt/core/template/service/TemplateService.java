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
     * sa sous-catégorie (voir {@link #rubriqueOf}). Les rubriques données gardent leur ordre (vides
     * comprises) ; celles qui reçoivent un élément sans y être sont ajoutées en fin,
     * {@value #RUBRIQUE_AUTRE} en dernier.
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
        // 1 — on récupère les templates : pas pour savoir quelles rubriques rendre (ce sont
        // rubriqueKeys), mais pour la règle de rangement — ce sont eux qui disent quelle rubrique
        // regroupe quelles catégories et sous-catégories.
        List<Template> templates = loadTemplates();

        // 2 — on regroupe les éléments par rubrique : pour chaque élément, sa sous-catégorie
        // (subcatOf) donne sa rubrique (rubriqueOf).
        Map<String, List<T>> itemsByRubrique = items.stream()
                .collect(Collectors.groupingBy(
                        item -> rubriqueOf(templates, eventType, subcatOf.apply(item)),  // 1. la clé de groupe : la rubrique de l'élément
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

    /**
     * Retourne la rubrique où entre une sous-catégorie dans un événement du type donné : celle du
     * template du type qui regroupe la sous-catégorie, sinon sa catégorie entière ; à défaut, la
     * même recherche dans tous les templates (catalogue des rubriques connues) ; sinon
     * {@value #RUBRIQUE_AUTRE}.
     *
     * @param eventType le type d'événement
     * @param subcatKey la sous-catégorie (celle du prestataire d'une réservation…)
     * @return la clé de la rubrique
     * @throws SousCategorieNotFoundException si la sous-catégorie n'existe pas
     */
    public String rubriqueOf(String eventType, String subcatKey) {
        return rubriqueOf(loadTemplates(), eventType, subcatKey);
    }

    /**
     * Retourne la position où insérer une rubrique qui manque à un événement : à son rang si elle
     * vient du template du type (avant la première rubrique présente qui la suit dans le template),
     * en fin sinon ; toujours avant {@value #RUBRIQUE_AUTRE}, qui reste en dernier.
     *
     * @param eventType    le type d'événement (son template donne le rang des rubriques)
     * @param rubriqueKeys les rubriques de l'événement, dans l'ordre d'affichage (sans la rubrique à insérer)
     * @param rubriqueKey  la rubrique à insérer (celle d'une réservation qui entre…)
     * @return la position d'insertion dans rubriqueKeys
     */
    public int insertionPosition(String eventType, List<String> rubriqueKeys, String rubriqueKey) {
        // RUBRIQUE_AUTRE va toujours en dernier.
        if (RUBRIQUE_AUTRE.equals(rubriqueKey)) return rubriqueKeys.size();

        // Rubrique du template : avant la première rubrique présente qui la suit dans le template.
        List<String> templateKeys = findTemplate(eventType).map(Template::rubriques).orElse(List.of()).stream()
                .map(TemplateRubrique::key)
                .toList();
        int rankInTemplate = templateKeys.indexOf(rubriqueKey);
        if (rankInTemplate >= 0) {
            for (String following : templateKeys.subList(rankInTemplate + 1, templateKeys.size())) {
                int position = rubriqueKeys.indexOf(following);
                if (position >= 0) return position;
            }
        }

        // Sinon (rubrique du catalogue, ou rien ne la suit) : en fin, avant RUBRIQUE_AUTRE s'il est là.
        int autrePosition = rubriqueKeys.indexOf(RUBRIQUE_AUTRE);
        return autrePosition >= 0 ? autrePosition : rubriqueKeys.size();
    }

    // Règle de rubriqueOf, sur des templates déjà chargés.
    private String rubriqueOf(List<Template> templates, String eventType, String subcatKey) {
        String categoryKey = categorieService.getCategoryKeyOf(subcatKey);
        List<TemplateRubrique> ownRubriques = templates.stream()
                .filter(template -> template.type().equals(eventType))
                .findFirst()
                .map(Template::rubriques)
                .orElse(List.of());
        List<TemplateRubrique> catalogue = templates.stream()
                .flatMap(template -> template.rubriques().stream())
                .toList();
        return findRubrique(ownRubriques, subcatKey, categoryKey)
                .or(() -> findRubrique(catalogue, subcatKey, categoryKey))
                .orElse(RUBRIQUE_AUTRE);
    }

    // Première rubrique qui regroupe la sous-catégorie, sinon sa catégorie entière.
    private Optional<String> findRubrique(List<TemplateRubrique> rubriques, String subcatKey, String categoryKey) {
        return rubriques.stream()
                .filter(rubrique -> rubrique.subcategories().contains(subcatKey))
                .findFirst()
                .or(
                        () -> rubriques.stream()
                                .filter(rubrique -> rubrique.categories().contains(categoryKey))
                                .findFirst()
                )
                .map(TemplateRubrique::key);
    }

    private Optional<Template> findTemplate(String eventType) {
        return loadTemplates().stream()
                .filter(template -> template.type().equals(eventType))
                .findFirst();
    }

    private List<Template> loadTemplates() {
        return templateDocumentRepository.findById(TemplateDocument.SINGLE_ID)
                .map(TemplateDocument::getContent)
                .orElse(List.of());
    }
}
