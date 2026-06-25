package net.franzka.sgilt.core.prestataire.dto;

import net.franzka.sgilt.core.prestataire.domain.Engagement;

import java.util.List;

/**
 * DTO de mise à jour de la fiche prestataire (PATCH).
 * Sémantique : null = ne pas toucher le champ, valeur/""/"[]" = écrire.
 * Les champs système (id, slug, categoryKey…) sont absents : id est dans le path, les autres
 * ne sont jamais écrits.
 */
public record PrestataireUpdateDto(
        String name,
        String baseline,
        String heroImage,
        String youtubeId,
        String shortDescription,
        List<String> photos,
        List<Engagement> badges,
        List<String> offerings,
        IdentityDto identity,
        String budget,
        List<TestimonialDto> testimonials,
        List<String> logistics,
        List<String> technical,
        List<FaqItemDto> faq
) {}
