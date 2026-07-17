package net.franzka.sgilt.core.prestataire.dto;

import net.franzka.sgilt.core.prestataire.domain.Engagement;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;

import java.util.List;

/**
 * DTO complet pour la fiche détail d'un prestataire.
 */
public record PrestataireDetailDto(
        String id,
        String name,
        String slug,
        String baseline,
        String avatar,
        String shortDescription,
        String categoryKey,
        List<String> subcatKeys,
        List<MediaDto> medias,
        List<Engagement> badges,
        List<String> offerings,
        IdentityDto identity,
        String budget,
        List<TestimonialDto> testimonials,
        List<DetailDto> details,
        List<FaqItemDto> faq,
        PrestataireStatus status
) {}
