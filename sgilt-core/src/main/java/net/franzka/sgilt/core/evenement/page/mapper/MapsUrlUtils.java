package net.franzka.sgilt.core.evenement.page.mapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Lien Google Maps vers le lieu d'un événement, tiré de sa localisation saisie librement : un lien
 * de partage (« https://maps.app.goo.gl/… »), une adresse ou un point GPS (« 48.5839,7.7455 »).
 */
public final class MapsUrlUtils {

    private static final String GOOGLE_MAPS_SEARCH_URL = "https://www.google.com/maps/search/?api=1&query=";

    private MapsUrlUtils() {}

    /**
     * Construit le lien Google Maps d'une localisation : un lien http(s) est repris tel quel, une
     * adresse ou un point GPS devient une recherche Google Maps.
     *
     * @param localisation la localisation saisie, ou null
     * @return le lien, ou null si la localisation est vide
     */
    public static String mapsUrl(String localisation) {
        if (localisation == null || localisation.isBlank()) return null;
        String value = localisation.strip();
        if (isHttpUrl(value)) return value;
        return GOOGLE_MAPS_SEARCH_URL + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // Seuls http et https passent tels quels : un « javascript:… » collé devient une simple recherche.
    private static boolean isHttpUrl(String value) {
        String lower = value.toLowerCase();
        return lower.startsWith("https://") || lower.startsWith("http://");
    }
}
