package net.franzka.sgilt.core.ficheia.service;

import com.openai.errors.OpenAIInvalidDataException;
import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIRetryableException;
import com.openai.errors.OpenAIServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.ficheia.domain.GenerationIa;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.exception.FicheIaEmptyResultException;
import net.franzka.sgilt.core.ficheia.exception.FicheIaGenerationFailedException;
import net.franzka.sgilt.core.ficheia.exception.FicheIaQuotaExhaustedException;
import net.franzka.sgilt.core.ficheia.exception.FicheIaTechnicalException;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

/**
 * Orchestre la génération IA de fiche : vérification du quota, appel du service OpenAI, sauvegarde
 * du résultat brut et décrément du quota.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FicheIaOrchestrationService {

    private final FicheIaGenerationService ficheIaGenerationService;
    private final GenerationIaService generationIaService;
    private final PrestataireService prestataireService;

    /**
     * Lance une génération IA de fiche pour le prestataire lié à l'utilisateur connecté.
     *
     * @param utilisateur l'utilisateur PRO connecté
     * @param url         l'URL du site à analyser
     * @return le résultat généré et le quota restant
     * @throws FicheIaQuotaExhaustedException  si le prestataire n'a plus d'essai disponible
     * @throws FicheIaTechnicalException        en cas d'échec technique de l'appel OpenAI (réessayable,
     *                                           aucun essai consommé)
     * @throws FicheIaGenerationFailedException si la génération n'a produit aucun résultat exploitable
     *                                           (un essai est tout de même consommé)
     */
    public FicheIaGenerationResultDto generate(Utilisateur utilisateur, String url) {
        Prestataire prestataire = prestataireService.getEntityByUtilisateurOwner(utilisateur);
        GenerationIa generationIa = generationIaService.getOrCreateFor(prestataire);

        if (!generationIaService.hasTriesLeft(generationIa)) {
            throw new FicheIaQuotaExhaustedException(prestataire.getId().toString());
        }

        FicheIaGenerationDto dto;
        try {
            dto = ficheIaGenerationService.generate(url);
        } catch (OpenAIIoException | OpenAIServiceException | OpenAIRetryableException e) {
            log.error("Échec technique de la génération IA de fiche pour {}", url, e);
            throw new FicheIaTechnicalException("Erreur technique lors de l'appel au service de génération IA", e);
        } catch (FicheIaEmptyResultException | OpenAIInvalidDataException e) {
            log.warn("Échec de génération IA de fiche pour {} — contenu insuffisant ou réponse invalide", url, e);
            generationIaService.recordFailedAttempt(generationIa);
            throw new FicheIaGenerationFailedException("La génération n'a pas produit de résultat exploitable pour " + url, e);
        }

        GenerationIa saved = generationIaService.recordSuccess(generationIa, dto, url);
        return new FicheIaGenerationResultDto(dto, saved.getTriesLeft(), saved.getLastGenerationDateTime());
    }

    /**
     * Récupère l'état courant de la génération IA pour le prestataire lié à l'utilisateur connecté,
     * en lecture seule (aucune ligne créée si aucune génération n'existe encore).
     *
     * @param utilisateur l'utilisateur PRO connecté
     * @return le dernier résultat généré (ou {@code null}), le quota restant et l'horodatage de la
     *         dernière génération (ou {@code null})
     */
    public FicheIaGenerationResultDto getState(Utilisateur utilisateur) {
        Prestataire prestataire = prestataireService.getEntityByUtilisateurOwner(utilisateur);
        return generationIaService.getState(prestataire);
    }
}
