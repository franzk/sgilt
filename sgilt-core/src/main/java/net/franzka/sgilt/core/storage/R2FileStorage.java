package net.franzka.sgilt.core.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

/**
 * Implémentation de {@link FileStorageService} via Cloudflare R2 (S3-compatible).
 * En local, pointe vers {@code sgilt-r2-mock}.
 * En staging/prod, pointe vers le bucket R2 configuré.
 */
@Service
@Slf4j
public class R2FileStorage implements FileStorageService {

    private final S3Client s3;
    private final String bucket;

    public R2FileStorage(
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
        log.info("R2FileStorage initialisé — endpoint={}, bucket={}", endpoint, bucket);
    }

    /**
     * Upload un fichier vers R2 sous le préfixe indiqué et retourne son chemin.
     *
     * @param file   le fichier à uploader
     * @param prefix le préfixe du chemin (ex. "uploads", "documents")
     * @return le chemin du fichier (ex. {@code uploads/uuid.jpg})
     * @throws IOException en cas d'erreur de communication avec R2
     */
    @Override
    public String upload(MultipartFile file, String prefix) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filePath = prefix + "/" + UUID.randomUUID() + (ext != null ? "." + ext : "");
        try {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(filePath)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            log.info("Upload R2 réussi — path={}", filePath);
            return filePath;
        } catch (S3Exception e) {
            log.error("Erreur upload R2", e);
            throw new IOException("Erreur de communication avec R2 lors de l'upload", e);
        }
    }

    /**
     * Supprime un fichier de R2 par son chemin.
     *
     * @param filePath le chemin du fichier à supprimer
     * @throws IOException en cas d'erreur de communication avec R2
     */
    @Override
    public void delete(String filePath) throws IOException {
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build());
        } catch (S3Exception e) {
            throw new IOException("Erreur de communication avec R2 lors de la suppression", e);
        }
    }

    /**
     * Retourne un flux de lecture vers le fichier identifié par son chemin.
     *
     * @param filePath le chemin du fichier dans le bucket
     * @return un InputStream vers le contenu du fichier
     * @throws IOException en cas d'erreur de communication avec R2
     */
    @Override
    public InputStream stream(String filePath) throws IOException {
        try {
            ResponseInputStream<GetObjectResponse> response = s3.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(filePath)
                            .build());
            return response;
        } catch (S3Exception e) {
            throw new IOException("Erreur de communication avec R2 lors du streaming", e);
        }
    }
}
