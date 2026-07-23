package net.franzka.sgilt.core.ficheia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.ficheia.domain.GenerationIa;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.exception.FicheIaNoResultAvailableException;
import net.franzka.sgilt.core.ficheia.repository.GenerationIaRepository;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service métier pour l'entité {@link GenerationIa}.
 */
@Service
@RequiredArgsConstructor
public class GenerationIaService {

    private final GenerationIaRepository generationIaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Charge la ligne de génération IA d'un prestataire, ou en crée une nouvelle (quota par
     * défaut) s'il s'agit de sa première génération.
     *
     * @param prestataire le prestataire concerné
     * @return la ligne de génération IA existante, ou une nouvelle instance non persistée
     */
    public GenerationIa getOrCreateFor(Prestataire prestataire) {
        return generationIaRepository.findByPrestataire(prestataire)
                .orElseGet(() -> GenerationIa.createFor(prestataire));
    }

    /**
     * Vérifie si le prestataire dispose encore d'au moins un essai de génération.
     *
     * @param generationIa la ligne de génération IA du prestataire
     * @return {@code true} si au moins un essai est disponible
     */
    public boolean hasTriesLeft(GenerationIa generationIa) {
        return generationIa.getTriesLeft() > 0;
    }

    /**
     * Enregistre le résultat d'une génération réussie : sauvegarde le JSON brut, l'horodatage et
     * l'URL source, puis décrémente le quota.
     *
     * @param generationIa la ligne de génération IA à mettre à jour
     * @param dto          le résultat généré, sérialisé tel quel
     * @param url          l'URL ayant servi à la génération
     * @return la ligne de génération IA sauvegardée
     */
    public GenerationIa recordSuccess(GenerationIa generationIa, FicheIaGenerationDto dto, String url) {
        generationIa.setLastGeneration(toJson(dto));
        generationIa.setLastGenerationDateTime(LocalDateTime.now());
        generationIa.setSourceUrl(url);
        generationIa.setTriesLeft(generationIa.getTriesLeft() - 1);
        return generationIaRepository.save(generationIa);
    }

    /**
     * Enregistre une tentative de génération échouée (échec lié aux données fournies) : décrémente
     * le quota comme un essai normal, sans toucher à {@code sourceUrl} ni au dernier résultat
     * exploitable déjà stocké — la paire {@code sourceUrl}/{@code lastGeneration} doit toujours
     * correspondre à la même génération réussie.
     *
     * @param generationIa la ligne de génération IA à mettre à jour
     * @return la ligne de génération IA sauvegardée
     */
    public GenerationIa recordFailedAttempt(GenerationIa generationIa) {
        generationIa.setTriesLeft(generationIa.getTriesLeft() - 1);
        return generationIaRepository.save(generationIa);
    }

    /**
     * Charge et désérialise le dernier résultat de génération IA exploitable d'un prestataire.
     *
     * @param prestataire le prestataire concerné
     * @return le contenu généré
     * @throws FicheIaNoResultAvailableException si aucune génération exploitable n'est disponible
     */
    public FicheIaGenerationDto getLastGeneration(Prestataire prestataire) {
        String lastGeneration = generationIaRepository.findByPrestataire(prestataire)
                .map(GenerationIa::getLastGeneration)
                .orElse(null);
        if (lastGeneration == null) {
            throw new FicheIaNoResultAvailableException(prestataire.getId().toString());
        }
        return fromJson(lastGeneration);
    }

    /**
     * Lit l'état courant de la génération IA d'un prestataire sans aucun effet de bord :
     * contrairement à {@link #getOrCreateFor}, ne crée jamais de ligne en base. À utiliser
     * uniquement pour une lecture (endpoint GET) — ne jamais appeler avant une opération qui doit
     * consommer le quota.
     *
     * @param prestataire le prestataire concerné
     * @return le dernier contenu généré (ou {@code null} si aucune génération n'existe), le quota
     *         restant (valeur par défaut si aucune ligne n'existe) et l'horodatage de la dernière
     *         génération (ou {@code null})
     */
    public FicheIaGenerationResultDto getState(Prestataire prestataire) {
        return generationIaRepository.findByPrestataire(prestataire)
                .map(g -> new FicheIaGenerationResultDto(
                        g.getLastGeneration() == null ? null : fromJson(g.getLastGeneration()),
                        g.getTriesLeft(),
                        g.getLastGenerationDateTime()))
                .orElseGet(() -> new FicheIaGenerationResultDto(null, GenerationIa.defaultTriesLeft(), null));
    }

    private String toJson(FicheIaGenerationDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Échec de sérialisation du résultat de génération IA", e);
        }
    }

    private FicheIaGenerationDto fromJson(String json) {
        try {
            return objectMapper.readValue(json, FicheIaGenerationDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Échec de désérialisation du résultat de génération IA", e);
        }
    }
}
