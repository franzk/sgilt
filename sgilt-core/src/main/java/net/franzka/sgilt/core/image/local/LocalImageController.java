package net.franzka.sgilt.core.image.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Expose les images stockées localement via {@code GET /images/{imageId}}.
 * Activé uniquement quand {@code sgilt.image.storage=local}.
 */
@RestController
@ConditionalOnProperty(name = "sgilt.image.storage", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalImageController {

    @Value("${sgilt.image.local.path:./local-images}")
    private String storagePath;

    /**
     * Sert le fichier image identifié par son imageId.
     * Retourne 400 si l'imageId tente une traversée de chemin, 404 si le fichier est absent.
     *
     * @param imageId l'identifiant de l'image
     * @return le fichier image avec le Content-Type approprié
     * @throws IOException en cas d'erreur de lecture
     */
    @GetMapping("/images/{imageId}")
    public ResponseEntity<Resource> serveImage(@PathVariable String imageId) throws IOException {
        Path base = Path.of(storagePath).toAbsolutePath().normalize();
        Path target = base.resolve(imageId).normalize();

        // Protection contre la traversée de chemin
        if (!target.startsWith(base)) {
            log.warn("Tentative de traversée de chemin détectée : {}", imageId);
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new FileSystemResource(target);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(target);
        MediaType mediaType = contentType != null
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}
