package net.franzka.sgilt.core.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction du stockage de fichiers (images et documents).
 */
public interface FileStorageService {

    /**
     * Stocke un fichier et retourne son chemin dans le bucket.
     *
     * @param file   le fichier à stocker
     * @param prefix le préfixe du chemin (ex. "uploads", "documents")
     * @return le chemin du fichier dans le bucket
     * @throws IOException en cas d'erreur d'écriture
     */
    String upload(MultipartFile file, String prefix) throws IOException;

    /**
     * Supprime le fichier identifié par son chemin.
     *
     * @param filePath le chemin du fichier à supprimer
     * @throws IOException en cas d'erreur de suppression
     */
    void delete(String filePath) throws IOException;

    /**
     * Retourne un flux de lecture vers le fichier identifié par son chemin.
     *
     * @param filePath le chemin du fichier dans le bucket
     * @return un InputStream vers le contenu du fichier
     * @throws IOException en cas d'erreur de lecture
     */
    InputStream stream(String filePath) throws IOException;
}
