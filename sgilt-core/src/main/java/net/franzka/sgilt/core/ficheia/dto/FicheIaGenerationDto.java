package net.franzka.sgilt.core.ficheia.dto;

import net.franzka.sgilt.core.prestataire.dto.DetailDto;
import net.franzka.sgilt.core.prestataire.dto.FaqItemDto;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import net.franzka.sgilt.core.prestataire.dto.TestimonialDto;

import java.util.List;

/**
 * DTO du contenu de fiche prestataire généré par IA, avec des champs alignés sur ceux de
 * l'entité {@code Prestataire} pour permettre un mapping direct dans les briques ultérieures.
 */
public record FicheIaGenerationDto(
        String shortDescription,
        String baseline,
        List<String> offerings,
        IdentityDto identity,
        List<TestimonialDto> testimonials,
        List<DetailDto> details,
        List<FaqItemDto> faq,
        String budget
) {}
