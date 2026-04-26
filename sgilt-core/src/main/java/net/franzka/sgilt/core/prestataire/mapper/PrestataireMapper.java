package net.franzka.sgilt.core.prestataire.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper MapStruct pour l'entité {@link Prestataire}.
 * Les champs JSONB (stockés en String) sont désérialisés via des méthodes @Named.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@Slf4j
public abstract class PrestataireMapper {

    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Mappe un prestataire vers sa représentation carte (liste de résultats).
     * Tous les champs sont des scalaires — génération automatique.
     *
     * @param prestataire l'entité source
     * @return le DTO carte
     */
    public abstract PrestataireCardDto toCardDto(Prestataire prestataire);

    /**
     * Mappe un prestataire vers sa fiche complète.
     * Les champs JSONB (String en base) sont désérialisés via les méthodes @Named ci-dessous.
     *
     * @param prestataire l'entité source
     * @return le DTO complet
     */
    @Mapping(target = "photos",       source = "photos",       qualifiedByName = "parseStringList")
    @Mapping(target = "badges",       source = "badges",       qualifiedByName = "parseBadgeList")
    @Mapping(target = "offerings",    source = "offerings",    qualifiedByName = "parseStringList")
    @Mapping(target = "identity",     source = "identity",     qualifiedByName = "parseIdentity")
    @Mapping(target = "budget",       source = "budget",       qualifiedByName = "parseJsonString")
    @Mapping(target = "testimonials", source = "testimonials", qualifiedByName = "parseTestimonialList")
    @Mapping(target = "logistics",    source = "logistics",    qualifiedByName = "parseStringList")
    @Mapping(target = "technical",    source = "technical",    qualifiedByName = "parseStringList")
    @Mapping(target = "faq",          source = "faq",          qualifiedByName = "parseFaqList")
    public abstract PrestataireDetailDto toDetailDto(Prestataire prestataire);

    // ── Convertisseurs JSONB ──────────────────────────────────────────────────

    @Named("parseStringList")
    protected List<String> parseStringList(String json) {
        return parseJson(json, new TypeReference<>() {});
    }

    @Named("parseBadgeList")
    protected List<BadgeDto> parseBadgeList(String json) {
        return parseJson(json, new TypeReference<>() {});
    }

    @Named("parseIdentity")
    protected IdentityDto parseIdentity(String json) {
        return parseJson(json, IdentityDto.class);
    }

    @Named("parseJsonString")
    protected String parseJsonString(String json) {
        return parseJson(json, String.class);
    }

    @Named("parseTestimonialList")
    protected List<TestimonialDto> parseTestimonialList(String json) {
        return parseJson(json, new TypeReference<>() {});
    }

    @Named("parseFaqList")
    protected List<FaqItemDto> parseFaqList(String json) {
        return parseJson(json, new TypeReference<>() {});
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Échec de désérialisation JSONB ({}): {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    private <T> T parseJson(String json, TypeReference<T> typeRef) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.warn("Échec de désérialisation JSONB: {}", e.getMessage());
            return null;
        }
    }
}
