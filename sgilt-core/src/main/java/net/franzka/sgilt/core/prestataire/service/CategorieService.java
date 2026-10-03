package net.franzka.sgilt.core.prestataire.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.domain.MoveDirection;
import net.franzka.sgilt.core.prestataire.domain.SousCategorieError;
import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieCreateRequest;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieUpdateRequest;
import net.franzka.sgilt.core.prestataire.exception.SousCategorieInvalidException;
import net.franzka.sgilt.core.prestataire.exception.SousCategorieNotFoundException;
import net.franzka.sgilt.core.prestataire.mapper.CategorieMapper;
import net.franzka.sgilt.core.prestataire.repository.CategorieRepository;
import net.franzka.sgilt.core.prestataire.repository.SousCategorieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service des catégories et sous-catégories prestataire : lecture publique et administration.
 */
@Service
@RequiredArgsConstructor
public class CategorieService {

    private static final Sort SORT_BY_POSITION = Sort.by("position");

    private final CategorieRepository categorieRepository;
    private final SousCategorieRepository sousCategorieRepository;
    private final CategorieMapper categorieMapper;
    private final PrestataireService prestataireService;

    /**
     * Retourne les catégories pour le public : triées, chacune avec ses sous-catégories
     * triées.
     *
     * @return les catégories
     */
    public List<CategorieDto> getCategories() {
        return categorieRepository.findAll(SORT_BY_POSITION).stream()
                .map(categorieMapper::toCategorieDto)
                .toList();
    }

    /**
     * Retourne les catégories pour le back-office, avec le nombre de prestataires par sous-catégorie.
     *
     * @return les catégories et leurs sous-catégories, triées
     */
    public List<CategorieAdminDto> getCategoriesAdmin() {
        List<Categorie> categories = categorieRepository.findAll(SORT_BY_POSITION);
        Map<String, Long> prestataireCounts = categories.stream()
                .flatMap(categorie -> categorie.getSubcategories().stream())
                .collect(Collectors.toMap(
                        SousCategorie::getKey,
                        sousCategorie -> prestataireService.countUsingSubcategory(sousCategorie.getKey())));
        return categories.stream()
                .map(categorie -> categorieMapper.toCategorieAdminDto(categorie, prestataireCounts))
                .toList();
    }

    /**
     * Crée une sous-catégorie en dernière position de sa catégorie, sans rubrique.
     *
     * @param request la clé, le libellé et la catégorie
     * @return la sous-catégorie créée
     * @throws SousCategorieInvalidException si la clé existe déjà ou si la catégorie est inconnue
     */
    public SousCategorieAdminDto createSousCategorie(SousCategorieCreateRequest request) {
        if (sousCategorieRepository.existsById(request.key())) {
            throw new SousCategorieInvalidException(SousCategorieError.KEY_ALREADY_EXISTS,
                    "La sous-catégorie '" + request.key() + "' existe déjà.");
        }
        ensureCategorieExists(request.categoryKey());

        SousCategorie sousCategorie = sousCategorieRepository.save(SousCategorie.builder()
                .key(request.key())
                .name(request.name())
                .categoryKey(request.categoryKey())
                .position(nextPosition(request.categoryKey()))
                .build());
        return categorieMapper.toSousCategorieAdminDto(sousCategorie, 0L);
    }

    /**
     * Modifie le libellé et la catégorie d'une sous-catégorie. Un changement de catégorie la place
     * en dernière position de la nouvelle catégorie ; ses prestataires la suivent (cascade en base).
     *
     * @param key     la clé de la sous-catégorie
     * @param request le nouveau libellé et la catégorie
     * @return la sous-catégorie modifiée
     * @throws SousCategorieNotFoundException si la sous-catégorie n'existe pas
     * @throws SousCategorieInvalidException    si la catégorie est inconnue
     */
    public SousCategorieAdminDto updateSousCategorie(String key, SousCategorieUpdateRequest request) {
        SousCategorie sousCategorie = findSousCategorie(key);
        sousCategorie.setName(request.name());

        if (!sousCategorie.getCategoryKey().equals(request.categoryKey())) {
            ensureCategorieExists(request.categoryKey());
            sousCategorie.setPosition(nextPosition(request.categoryKey()));
            sousCategorie.setCategoryKey(request.categoryKey());
        }
        return toAdminDtoWithPrestataireCount(sousCategorieRepository.save(sousCategorie));
    }

    /**
     * Échange une sous-catégorie avec sa voisine dans l'ordre d'affichage de sa catégorie. Sans
     * effet si elle est déjà en tête (vers le haut) ou en fin (vers le bas).
     *
     * @param key       la clé de la sous-catégorie
     * @param direction le sens du déplacement
     * @return la sous-catégorie déplacée
     * @throws SousCategorieNotFoundException si la sous-catégorie n'existe pas
     */
    public SousCategorieAdminDto moveSousCategorie(String key, MoveDirection direction) {
        SousCategorie sousCategorie = findSousCategorie(key);
        List<SousCategorie> siblings =
                sousCategorieRepository.findByCategoryKeyOrderByPositionAsc(sousCategorie.getCategoryKey());
        int index = siblings.indexOf(sousCategorie);
        int neighbourIndex = direction == MoveDirection.UP ? index - 1 : index + 1;
        if (neighbourIndex < 0 || neighbourIndex >= siblings.size()) return toAdminDtoWithPrestataireCount(sousCategorie);

        SousCategorie neighbour = siblings.get(neighbourIndex);
        int position = sousCategorie.getPosition();
        sousCategorie.setPosition(neighbour.getPosition());
        neighbour.setPosition(position);
        sousCategorieRepository.saveAll(List.of(sousCategorie, neighbour));
        return toAdminDtoWithPrestataireCount(sousCategorie);
    }

    /**
     * Retourne la catégorie d'une sous-catégorie.
     *
     * @param subcatKey la clé de la sous-catégorie
     * @return la clé de sa catégorie
     * @throws SousCategorieNotFoundException si la sous-catégorie n'existe pas
     */
    public String getCategoryKeyOf(String subcatKey) {
        return findSousCategorie(subcatKey).getCategoryKey();
    }

    /**
     * Supprime une sous-catégorie qu'aucun prestataire n'utilise.
     *
     * @param key la clé de la sous-catégorie
     * @throws SousCategorieNotFoundException si la sous-catégorie n'existe pas
     * @throws SousCategorieInvalidException    si des prestataires l'utilisent
     */
    public void deleteSousCategorie(String key) {
        SousCategorie sousCategorie = findSousCategorie(key);
        long prestataireCount = prestataireService.countUsingSubcategory(key);
        if (prestataireCount > 0) {
            throw new SousCategorieInvalidException(SousCategorieError.SOUS_CATEGORIE_IN_USE,
                    "La sous-catégorie '" + key + "' est utilisée par " + prestataireCount + " prestataire(s).");
        }
        sousCategorieRepository.delete(sousCategorie);
    }

    // ── Lookup internes ───────────────────────────────────────────────────────

    /**
     * Construit le DTO d'administration d'une sous-catégorie, avec le nombre de prestataires qui
     * l'utilisent (compté en base au moment de l'appel).
     *
     * @param sousCategorie la sous-catégorie
     * @return le DTO d'administration, nombre de prestataires compris
     */
    private SousCategorieAdminDto toAdminDtoWithPrestataireCount(SousCategorie sousCategorie) {
        return categorieMapper.toSousCategorieAdminDto(
                sousCategorie, prestataireService.countUsingSubcategory(sousCategorie.getKey()));
    }

    private SousCategorie findSousCategorie(String key) {
        return sousCategorieRepository.findById(key)
                .orElseThrow(() -> new SousCategorieNotFoundException(key));
    }

    /**
     * Vérifie que la catégorie visée par une création ou un déplacement de sous-catégorie existe.
     *
     * @param categoryKey la clé de la catégorie, issue de la requête
     * @throws SousCategorieInvalidException si elle n'existe pas (requête invalide, 400)
     */
    private void ensureCategorieExists(String categoryKey) {
        if (!categorieRepository.existsById(categoryKey)) {
            throw new SousCategorieInvalidException(SousCategorieError.UNKNOWN_CATEGORIE,
                    "Catégorie inconnue : " + categoryKey);
        }
    }

    /** Position qui place une sous-catégorie après les autres de la catégorie. */
    private int nextPosition(String categoryKey) {
        return sousCategorieRepository.findByCategoryKeyOrderByPositionAsc(categoryKey).stream()
                .map(SousCategorie::getPosition)
                .max(Integer::compare)
                .map(max -> max + 1)
                .orElse(1);
    }
}
