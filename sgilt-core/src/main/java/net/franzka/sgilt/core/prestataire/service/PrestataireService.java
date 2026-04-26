package net.franzka.sgilt.core.prestataire.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.*;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service métier pour l'entité {@link Prestataire}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrestataireService {

    private final PrestataireRepository prestataireRepository;
    private final PrestataireMapper prestataireMapper;

    /**
     * Charge un prestataire par son identifiant.
     *
     * @param id l'identifiant du prestataire
     * @return le prestataire correspondant
     * @throws EntityNotFoundException si aucun prestataire ne correspond
     */
    public Prestataire getById(UUID id) {
        return prestataireRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Prestataire with id {} not found", id);
                    return new EntityNotFoundException();
                });
    }

    /**
     * Charge la fiche complète d'un prestataire par son slug.
     *
     * @param slug le slug du prestataire
     * @return le DTO complet
     * @throws PrestataireNotFoundException si aucun prestataire ne correspond
     */
    public PrestataireDetailDto getBySlug(String slug) {
        Prestataire p = prestataireRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new PrestataireNotFoundException(slug));
        return prestataireMapper.toDetailDto(p);
    }

    /**
     * Recherche des prestataires avec un filtre exclusif : soit par catégorie, soit par
     * sous-catégories, jamais les deux simultanément.
     * Calcule toujours les compteurs globaux par catégorie (pour les tabs de navigation),
     * et les compteurs par sous-catégorie pour la catégorie active.
     *
     * @param categoryKey clé de catégorie (null si filtre par subcatKeys)
     * @param subcatKeys  clés de sous-catégories (null/vide si filtre par categoryKey)
     * @return réponse avec résultats, compteurs catégorie et compteurs sous-catégorie
     */
    public PrestataireSearchResponseDto search(String categoryKey, List<String> subcatKeys) {
        List<Prestataire> all = prestataireRepository.findByDeletedAtIsNull();

        List<Prestataire> filtered = resolveFiltered(categoryKey, subcatKeys);
        String activeCategoryKey = resolveActiveCategoryKey(categoryKey, subcatKeys, filtered);

        return new PrestataireSearchResponseDto(
                filtered.stream().map(prestataireMapper::toCardDto).toList(),
                buildCategoryCounts(all),
                buildSubcatCounts(all, activeCategoryKey)
        );
    }

    // ── Résolution du filtre exclusif ─────────────────────────────────────────

    private List<Prestataire> resolveFiltered(String categoryKey, List<String> subcatKeys) {
        if (subcatKeys != null && !subcatKeys.isEmpty()) {
            return prestataireRepository.findBySubcatKeysInAndDeletedAtIsNull(subcatKeys);
        }
        if (categoryKey != null) {
            return prestataireRepository.findByCategoryKeyAndDeletedAtIsNull(categoryKey);
        }
        return prestataireRepository.findByDeletedAtIsNull();
    }

    private String resolveActiveCategoryKey(String categoryKey, List<String> subcatKeys, List<Prestataire> filtered) {
        if (categoryKey != null) return categoryKey;
        if (subcatKeys != null && !subcatKeys.isEmpty()) {
            // Dérivé depuis les résultats : tous les prestataires d'une même catégorie
            return filtered.stream()
                    .map(Prestataire::getCategoryKey)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // ── Compteurs ─────────────────────────────────────────────────────────────

    private Map<String, Long> buildCategoryCounts(List<Prestataire> all) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("all", (long) all.size());
        all.forEach(p -> counts.merge(p.getCategoryKey(), 1L, Long::sum));
        return counts;
    }

    private Map<String, Long> buildSubcatCounts(List<Prestataire> all, String activeCategoryKey) {
        if (activeCategoryKey == null) return new HashMap<>();
        Map<String, Long> counts = new HashMap<>();
        all.stream()
                .filter(p -> activeCategoryKey.equals(p.getCategoryKey()))
                .forEach(p -> p.getSubcatKeys().forEach(key -> counts.merge(key, 1L, Long::sum)));
        return counts;
    }
}
