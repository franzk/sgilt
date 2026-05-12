package net.franzka.sgilt.core.image.local;

import net.franzka.sgilt.core.image.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Implémentation locale de {@link ImageStorageService}.
 * Stocke les fichiers sur le système de fichiers du serveur.
 * Activée quand {@code sgilt.image.storage=local} (valeur par défaut).
 */
@Service
@ConditionalOnProperty(name = "sgilt.image.storage", havingValue = "local", matchIfMissing = true)
public class LocalImageStorage implements ImageStorageService {

    @Value("${sgilt.image.local.path:./local-images}")
    private String storagePath;

    @Value("${sgilt.image.base-url:http://localhost:5027}")
    private String baseUrl;

    /**
     * Stocke le fichier dans le dossier local et retourne un imageId de la forme {@code uuid.ext}.
     *
     * @param file le fichier à stocker
     * @return l'imageId unique du fichier stocké
     * @throws IOException en cas d'erreur d'écriture
     */
    @Override
    public String upload(MultipartFile file) throws IOException {
        String extension = extractExtension(file.getOriginalFilename());
        String imageId = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path target = Path.of(storagePath).resolve(imageId);
        Files.createDirectories(target.getParent());
        file.transferTo(target);
        return imageId;
    }

    /**
     * Supprime le fichier correspondant à l'imageId donné. Sans effet si le fichier est absent.
     *
     * @param imageId l'identifiant de l'image à supprimer
     * @throws IOException en cas d'erreur de suppression
     */
    @Override
    public void delete(String imageId) throws IOException {
        Files.deleteIfExists(Path.of(storagePath).resolve(imageId));
    }

    /**
     * Retourne l'URL publique de l'image sous la forme {@code {baseUrl}/images/{imageId}}.
     *
     * @param imageId l'identifiant de l'image
     * @return l'URL d'accès à l'image
     */
    @Override
    public String toUrl(String imageId) {
        return baseUrl + "/images/" + imageId;
    }

    private static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
