package net.franzka.sgilt.core.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Implémentation de {@link ImageStorageService} via l'API Cloudflare Images.
 * En local, pointe vers le service mock {@code sgilt-cf-images-mock}.
 * En staging/prod, pointe vers CF Images réel.
 */
@Service
@Slf4j
public class CloudflareImageStorage implements ImageStorageService {

    private final CfImagesClient client;
    private final String deliveryUrl;
    private final String variant;

    public CloudflareImageStorage(
            CfImagesClient client,
            @Value("${sgilt.cf-images.delivery-url}") String deliveryUrl,
            @Value("${sgilt.cf-images.variant:public}") String variant
    ) {
        this.client      = client;
        this.deliveryUrl = deliveryUrl;
        this.variant     = variant;
    }

    /**
     * Upload un fichier vers Cloudflare Images et retourne l'imageId attribué par CF.
     *
     * @param file le fichier à uploader
     * @return l'imageId unique attribué
     * @throws IOException en cas d'erreur de communication avec l'API
     */
    @Override
    public String upload(MultipartFile file) throws IOException {
        try {
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override public String getFilename() { return file.getOriginalFilename(); }
            };
            CfImagesClient.UploadResponse response = client.upload(resource);

            if (!response.success() || response.result() == null) {
                throw new IOException("CF Images upload échoué : réponse invalide");
            }
            log.info("Upload réussi — imageId={}", response.result().id());
            return response.result().id();
        } catch (RestClientException e) {
            log.error("Erreur upload CF Images", e);
            throw new IOException("Erreur de communication avec CF Images lors de l'upload", e);
        }
    }

    /**
     * Supprime une image depuis Cloudflare Images par son imageId.
     * Sans effet si l'image est introuvable côté CF (4xx ignoré).
     *
     * @param imageId l'identifiant de l'image à supprimer
     * @throws IOException en cas d'erreur de communication avec l'API
     */
    @Override
    public void delete(String imageId) throws IOException {
        try {
            client.delete(imageId);
        } catch (RestClientException e) {
            throw new IOException("Erreur de communication avec CF Images lors de la suppression", e);
        }
    }

    /**
     * Construit l'URL de livraison CF Images pour un imageId donné.
     * Format : {@code {deliveryUrl}/{imageId}/{variant}}
     *
     * @param imageId l'identifiant de l'image
     * @return l'URL complète utilisable par le front
     */
    @Override
    public String toUrl(String imageId) {
        return deliveryUrl + "/" + imageId + "/" + variant;
    }
}
