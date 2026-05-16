package net.franzka.sgilt.core.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * Implémentation de {@link ImageStorageService} via Cloudflare R2 (S3-compatible).
 * En local, pointe vers {@code sgilt-r2-mock}.
 * En staging/prod, pointe vers le bucket R2 configuré.
 */
@Service
@Slf4j
public class R2ImageStorage implements ImageStorageService {

    private final S3Client s3;
    private final String bucket;

    public R2ImageStorage(
            @Value("${sgilt.r2.endpoint}") String endpoint,
            @Value("${sgilt.r2.access-key-id}") String accessKeyId,
            @Value("${sgilt.r2.secret-access-key}") String secretAccessKey,
            @Value("${sgilt.r2.bucket}") String bucket
    ) {
        this.bucket = bucket;
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(Region.of("auto"))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
        log.info("R2ImageStorage initialisé — endpoint={}, bucket={}", endpoint, bucket);
    }

    /**
     * Upload un fichier vers R2 et retourne son chemin dans le bucket.
     *
     * @param file le fichier à uploader
     * @return le chemin de l'image (ex. {@code uploads/uuid.jpg})
     * @throws IOException en cas d'erreur de communication avec R2
     */
    @Override
    public String upload(MultipartFile file) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String imagePath = "uploads/" + UUID.randomUUID() + (ext != null ? "." + ext : "");
        try {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(imagePath)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            log.info("Upload R2 réussi — path={}", imagePath);
            return imagePath;
        } catch (S3Exception e) {
            log.error("Erreur upload R2", e);
            throw new IOException("Erreur de communication avec R2 lors de l'upload", e);
        }
    }

    /**
     * Supprime une image de R2 par son chemin.
     * Sans effet si l'image est introuvable (R2 retourne 204 dans tous les cas).
     *
     * @param imagePath le chemin de l'image à supprimer
     * @throws IOException en cas d'erreur de communication avec R2
     */
    @Override
    public void delete(String imagePath) throws IOException {
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(imagePath)
                    .build());
        } catch (S3Exception e) {
            throw new IOException("Erreur de communication avec R2 lors de la suppression", e);
        }
    }

}
