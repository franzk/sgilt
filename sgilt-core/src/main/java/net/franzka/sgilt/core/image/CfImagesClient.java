package net.franzka.sgilt.core.image;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Client déclaratif vers l'API d'upload Cloudflare Images.
 * En local, cible {@code sgilt-cf-images-mock}. En prod, cible CF Images réel.
 */
@HttpExchange
public interface CfImagesClient {

    /**
     * Upload un fichier image et retourne la réponse CF avec l'imageId attribué.
     *
     * @param file le fichier à uploader
     * @return la réponse d'upload CF
     */
    @PostExchange(contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    CfImagesClient.UploadResponse upload(@RequestPart("file") Resource file);

    /**
     * Supprime une image par son imageId.
     *
     * @param imageId l'identifiant de l'image à supprimer
     */
    @DeleteExchange("/{imageId}")
    void delete(@PathVariable String imageId);

    record UploadResult(String id) {}

    record UploadResponse(UploadResult result, boolean success) {}
}
