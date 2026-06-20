package net.franzka.sgilt.core.prestataire.dto;

import net.franzka.sgilt.core.prestataire.domain.Engagement;

import java.util.List;

/**
 * DTO complet pour la fiche détail d'un prestataire.
 */
public record PrestataireDetailDto(
        String id,
        String name,
        String slug,
        String baseline,
        String heroImage,
        String youtubeId,
        String shortDescription,
        String categoryKey,
        List<String> subcatKeys,
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
