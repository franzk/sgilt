package net.franzka.sgilt.core.ficheia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.ficheia.domain.GenerationIa;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
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

    private String toJson(FicheIaGenerationDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Échec de sérialisation du résultat de génération IA", e);
        }
    }
}
