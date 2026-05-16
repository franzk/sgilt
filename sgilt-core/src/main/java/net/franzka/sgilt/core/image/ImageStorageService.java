package net.franzka.sgilt.core.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Abstraction du stockage d'images.
 * Les implémentations concrètes sont sélectionnées via la propriété {@code sgilt.image.storage}.
 */
public interface ImageStorageService {

    /**
     * Stocke un fichier image et retourne son identifiant unique.
     *
     * @param file le fichier à stocker
     * @return l'identifiant de l'image (imageId) permettant de la référencer et de la supprimer
     * @throws IOException en cas d'erreur d'écriture
     */
    String upload(MultipartFile file) throws IOException;

    /**
     * Supprime l'image identifiée par son imageId.
     * Sans effet si l'image n'existe pas.
     *
     * @param imageId l'identifiant de l'image à supprimer
     * @throws IOException en cas d'erreur de suppression
     */
    void delete(String imageId) throws IOException;

}
