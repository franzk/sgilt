package net.franzka.sgilt.core.prestataire.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.franzka.sgilt.core.prestataire.dto.MediaDto;

import java.util.Comparator;
import java.util.List;

/**
 * Utilitaire statique pour l'extraction de données depuis le champ JSONB {@code medias}.
 */
public final class MediaUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MediaUtils() {}

    /**
     * Retourne le {@code ref} du média en position minimale (hero), ou {@code null} si absent.
     *
     * @param mediasJson le champ JSONB {@code medias} sérialisé en String
     * @return le ref du média héro, ou null
     */
    public static String heroImageRef(String mediasJson) {
        if (mediasJson == null) return null;
        try {
            List<MediaDto> list = OBJECT_MAPPER.readValue(mediasJson, new TypeReference<>() {});
            return list.stream()
                    .min(Comparator.comparingInt(MediaDto::position))
                    .map(MediaDto::ref)
                    .orElse(null);
        } catch (Exception _) {
            return null;
        }
    }
}
