package net.franzka.sgilt.core.prestataire.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.domain.Engagement;
import net.franzka.sgilt.core.prestataire.domain.MediaUtils;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Comparator;
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
    @Mapping(target = "heroImage", source = "medias", qualifiedByName = "resolveHeroImage")
    public abstract PrestataireCardDto toCardDto(Prestataire prestataire);

    /**
     * Mappe un prestataire vers sa représentation allégée pour le back-office admin.
     * Tous les champs sont des scalaires — génération automatique.
     *
     * @param prestataire l'entité source
     * @return le DTO liste admin
     */
    public abstract PrestataireAdminListItemDto toAdminListItemDto(Prestataire prestataire);

    /**
     * Mappe un prestataire vers sa fiche complète.
     * Les champs JSONB (String en base) sont désérialisés via les méthodes @Named ci-dessous.
     *
     * @param prestataire l'entité source
     * @return le DTO complet
     */
    @Mapping(target = "medias",        source = "medias",       qualifiedByName = "parseMediaList")
    @Mapping(target = "badges",       source = "badges",       qualifiedByName = "parseEngagementList")
    @Mapping(target = "offerings",    source = "offerings",    qualifiedByName = "parseStringList")
    @Mapping(target = "identity",     source = "identity",     qualifiedByName = "parseIdentity")
    @Mapping(target = "budget",       source = "budget",       qualifiedByName = "parseJsonString")
    @Mapping(target = "testimonials", source = "testimonials", qualifiedByName = "parseTestimonialList")
    @Mapping(target = "logistics",    source = "logistics",    qualifiedByName = "parseStringList")
    @Mapping(target = "technical",    source = "technical",    qualifiedByName = "parseStringList")
    @Mapping(target = "faq",          source = "faq",          qualifiedByName = "parseFaqList")
    public abstract PrestataireDetailDto toDetailDto(Prestataire prestataire);

    /**
     * Applique les champs non-null du DTO sur l'entité chargée.
     * Les champs null sont ignorés (nullValuePropertyMappingStrategy = IGNORE).
     * Les champs système ne sont jamais écrasés.
     *
     * @param prestataire l'entité cible (modifiée en place)
     * @param dto         les modifications à appliquer
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "slug",        ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "categoryKey", ignore = true)
    @Mapping(target = "subcatKeys",  ignore = true)
    @Mapping(target = "avatar",      ignore = true)
    @Mapping(target = "medias",      ignore = true)
    @Mapping(target = "status",      ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "deletedAt",   ignore = true)
    @Mapping(target = "badges",       source = "badges",       qualifiedByName = "serializeEngagementList")
    @Mapping(target = "offerings",    source = "offerings",    qualifiedByName = "serializeStringList")
    @Mapping(target = "identity",     source = "identity",     qualifiedByName = "serializeToJson")
    @Mapping(target = "budget",       source = "budget",       qualifiedByName = "serializeToJson")
    @Mapping(target = "testimonials", source = "testimonials", qualifiedByName = "serializeToJson")
    @Mapping(target = "logistics",    source = "logistics",    qualifiedByName = "serializeStringList")
    @Mapping(target = "technical",    source = "technical",    qualifiedByName = "serializeStringList")
    @Mapping(target = "faq",          source = "faq",          qualifiedByName = "serializeToJson")
    public abstract void updatePrestataire(@MappingTarget Prestataire prestataire, PrestataireUpdateDto dto);

    // ── Convertisseurs JSONB ──────────────────────────────────────────────────

    @Named("resolveHeroImage")
    protected String resolveHeroImage(String mediasJson) {
        return MediaUtils.heroImageRef(mediasJson);
    }

    @Named("parseStringList")
    protected List<String> parseStringList(String json) {
        return parseJson(json, new TypeReference<>() {});
    }

    @Named("parseMediaList")
    protected List<MediaDto> parseMediaList(String json) {
        List<MediaDto> list = parseJson(json, new TypeReference<>() {});
        if (list == null) return new ArrayList<>();
        return list.stream().sorted(Comparator.comparingInt(MediaDto::position)).toList();
    }

    @Named("parseEngagementList")
    protected List<Engagement> parseEngagementList(String json) {
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

    // ── Sérialiseurs JSONB (pour updatePrestataire) ───────────────────────────

    @Named("serializeStringList")
    protected String serializeStringList(List<String> list) {
        return serializeJson(list);
    }

    @Named("serializeEngagementList")
    protected String serializeEngagementList(List<Engagement> list) {
        return serializeJson(list);
    }

    @Named("serializeToJson")
    protected String serializeToJson(Object value) {
        return serializeJson(value);
    }

    private String serializeJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.warn("Échec de sérialisation JSONB: {}", e.getMessage());
            return null;
        }
    }
}
