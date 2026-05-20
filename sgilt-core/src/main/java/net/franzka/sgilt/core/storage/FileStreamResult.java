package net.franzka.sgilt.core.storage;

import org.springframework.http.MediaType;

import java.io.InputStream;

/**
 * Résultat d'un streaming de fichier depuis le stockage.
 * Encapsule le flux, le nom du fichier et le type MIME résolu.
 */
public record FileStreamResult(InputStream inputStream, String fileName, String mimeType) {

    /**
     * Retourne le {@link MediaType} correspondant au mimeType,
     * avec fallback sur {@code application/octet-stream} si absent ou invalide.
     */
    public MediaType resolvedMediaType() {
        try {
            return mimeType != null
                    ? MediaType.parseMediaType(mimeType)
                    : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
