package net.franzka.sgilt.core.ficheia.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.ficheia.domain.FicheIaAction;
import net.franzka.sgilt.core.ficheia.domain.FicheIaSection;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.exception.FicheIaInvalidInstructionException;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

/**
 * Applique le contenu de la dernière génération IA à la fiche du prestataire connecté, section par
 * section ou intégralement, sur instruction explicite de l'utilisateur. Les valeurs appliquées sont
 * toujours lues côté serveur depuis {@code GenerationIa.lastGeneration} — jamais transmises par
 * l'appelant.
 */
@Service
@RequiredArgsConstructor
public class FicheIaApplyService {

    private final GenerationIaService generationIaService;
    private final PrestataireService prestataireService;

    /**
     * Applique une instruction (section + action) au prestataire lié à l'utilisateur connecté.
     *
     * @param utilisateur l'utilisateur PRO connecté
     * @param section     la section à appliquer, {@code null} uniquement pour ECRASER_TOUT
     * @param action      REMPLACER, AJOUTER ou ECRASER_TOUT
     * @return la fiche complète après application
     * @throws FicheIaInvalidInstructionException si la combinaison section/action est invalide
     */
    public PrestataireDetailDto apply(Utilisateur utilisateur, FicheIaSection section, FicheIaAction action) {
        validate(section, action);

        Prestataire prestataire = prestataireService.getEntityByUtilisateurOwner(utilisateur);
        FicheIaGenerationDto generated = generationIaService.getLastGeneration(prestataire);

        return action == FicheIaAction.ECRASER_TOUT
                ? applyAll(prestataire, generated)
                : applyOne(prestataire, section, action, generated);
    }

    private void validate(FicheIaSection section, FicheIaAction action) {
        if (action == FicheIaAction.ECRASER_TOUT) {
            if (section != null) {
                throw new FicheIaInvalidInstructionException("ECRASER_TOUT ne prend pas de section");
            }
            return;
        }
        if (section == null) {
            throw new FicheIaInvalidInstructionException("Une section est requise pour l'action " + action);
        }
        if (action == FicheIaAction.AJOUTER && !section.isList()) {
            throw new FicheIaInvalidInstructionException("AJOUTER n'est pas valide pour la section " + section);
        }
    }

    private PrestataireDetailDto applyOne(Prestataire prestataire, FicheIaSection section, FicheIaAction action, FicheIaGenerationDto generated) {
        boolean append = action == FicheIaAction.AJOUTER;
        return switch (section) {
            case BASELINE -> prestataireService.replaceBaseline(prestataire, generated.baseline());
            case SHORT_DESCRIPTION -> prestataireService.replaceShortDescription(prestataire, generated.shortDescription());
            case IDENTITY -> prestataireService.replaceIdentity(prestataire, generated.identity());
            case BUDGET -> prestataireService.replaceBudget(prestataire, generated.budget());
            case OFFERINGS -> append
                    ? prestataireService.appendOfferings(prestataire, generated.offerings())
                    : prestataireService.replaceOfferings(prestataire, generated.offerings());
            case TESTIMONIALS -> append
                    ? prestataireService.appendTestimonials(prestataire, generated.testimonials())
                    : prestataireService.replaceTestimonials(prestataire, generated.testimonials());
            case DETAILS -> append
                    ? prestataireService.appendDetails(prestataire, generated.details())
                    : prestataireService.replaceDetails(prestataire, generated.details());
            case FAQ -> append
                    ? prestataireService.appendFaq(prestataire, generated.faq())
                    : prestataireService.replaceFaq(prestataire, generated.faq());
        };
    }

    private PrestataireDetailDto applyAll(Prestataire prestataire, FicheIaGenerationDto generated) {
        prestataireService.replaceBaseline(prestataire, generated.baseline());
        prestataireService.replaceShortDescription(prestataire, generated.shortDescription());
        prestataireService.replaceIdentity(prestataire, generated.identity());
        prestataireService.replaceBudget(prestataire, generated.budget());
        prestataireService.replaceOfferings(prestataire, generated.offerings());
        prestataireService.replaceTestimonials(prestataire, generated.testimonials());
        prestataireService.replaceDetails(prestataire, generated.details());
        return prestataireService.replaceFaq(prestataire, generated.faq());
    }
}
