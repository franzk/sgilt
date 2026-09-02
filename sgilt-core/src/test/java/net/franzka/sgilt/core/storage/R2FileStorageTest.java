package net.franzka.sgilt.core.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R2FileStorageTest {

    private static final String BUCKET = "sgilt-bucket";
    private static final String DOCUMENTS_BUCKET = "sgilt-documents-bucket";

    @Mock
    private S3Client s3Client;

    private R2FileStorage r2FileStorage;

    @BeforeEach
    void setUp() {
        r2FileStorage = new R2FileStorage(
                "http://localhost:9000", "access-key", "secret-key", BUCKET, DOCUMENTS_BUCKET);
        ReflectionTestUtils.setField(r2FileStorage, "s3", s3Client);
    }

    // -------------------------------------------------------------------------
    // upload
    // -------------------------------------------------------------------------

    @Nested
    class Upload {

        @Test
        void givenFileWithExtension_whenUpload_thenPathKeepsExtensionAndUsesMainBucket() throws IOException {
            var file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "contenu".getBytes());

            String path = r2FileStorage.upload(file, "uploads");

            assertThat(path).startsWith("uploads/").endsWith(".jpg");
            var captor = org.mockito.ArgumentCaptor.forClass(PutObjectRequest.class);
            verify(s3Client).putObject(captor.capture(), any(software.amazon.awssdk.core.sync.RequestBody.class));
            assertThat(captor.getValue().bucket()).isEqualTo(BUCKET);
            assertThat(captor.getValue().key()).isEqualTo(path);
            assertThat(captor.getValue().contentType()).isEqualTo("image/jpeg");
            assertThat(captor.getValue().contentLength()).isEqualTo((long) "contenu".getBytes().length);
        }

        @Test
        void givenFileWithoutExtension_whenUpload_thenPathHasNoExtension() throws IOException {
            var file = new MockMultipartFile("file", "noext", "application/octet-stream", "contenu".getBytes());

            String path = r2FileStorage.upload(file, "uploads");

            assertThat(path).matches("uploads/[0-9a-f-]{36}");
        }

        @Test
        void givenS3Failure_whenUpload_thenThrowsIOException() {
            var file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "contenu".getBytes());
            when(s3Client.putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class)))
                    .thenThrow(S3Exception.builder().message("panne R2").build());

            assertThatThrownBy(() -> r2FileStorage.upload(file, "uploads")).isInstanceOf(IOException.class);
        }
    }

    // -------------------------------------------------------------------------
    // uploadDocument
    // -------------------------------------------------------------------------

    @Nested
    class UploadDocument {

        @Test
        void givenDocument_whenUploadDocument_thenUsesDocumentsBucket() throws IOException {
            var file = new MockMultipartFile("file", "devis.pdf", "application/pdf", "contenu".getBytes());

            String path = r2FileStorage.uploadDocument(file, "reservation-feed");

            assertThat(path).startsWith("reservation-feed/").endsWith(".pdf");
            var captor = org.mockito.ArgumentCaptor.forClass(PutObjectRequest.class);
            verify(s3Client).putObject(captor.capture(), any(software.amazon.awssdk.core.sync.RequestBody.class));
            assertThat(captor.getValue().bucket()).isEqualTo(DOCUMENTS_BUCKET);
        }
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Nested
    class Delete {

        @Test
        void givenFilePath_whenDelete_thenDeletesFromMainBucket() throws IOException {
            r2FileStorage.delete("uploads/abc.jpg");

            var captor = org.mockito.ArgumentCaptor.forClass(DeleteObjectRequest.class);
            verify(s3Client).deleteObject(captor.capture());
            assertThat(captor.getValue().bucket()).isEqualTo(BUCKET);
            assertThat(captor.getValue().key()).isEqualTo("uploads/abc.jpg");
        }

        @Test
        void givenS3Failure_whenDelete_thenThrowsIOException() {
            when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .thenThrow(S3Exception.builder().message("panne R2").build());

            assertThatThrownBy(() -> r2FileStorage.delete("uploads/abc.jpg")).isInstanceOf(IOException.class);
        }
    }

    // -------------------------------------------------------------------------
    // deleteDocument
    // -------------------------------------------------------------------------

    @Nested
    class DeleteDocument {

        @Test
        void givenFilePath_whenDeleteDocument_thenDeletesFromDocumentsBucket() throws IOException {
            r2FileStorage.deleteDocument("reservation-feed/devis.pdf");

            var captor = org.mockito.ArgumentCaptor.forClass(DeleteObjectRequest.class);
            verify(s3Client).deleteObject(captor.capture());
            assertThat(captor.getValue().bucket()).isEqualTo(DOCUMENTS_BUCKET);
        }
    }

    // -------------------------------------------------------------------------
    // streamDocument
    // -------------------------------------------------------------------------

    @Nested
    class StreamDocument {

        @Test
        void givenFilePath_whenStreamDocument_thenReturnsStreamFromDocumentsBucket() throws IOException {
            var responseStream = new ResponseInputStream<>(
                    GetObjectResponse.builder().build(), new ByteArrayInputStream("contenu".getBytes()));
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

            var result = r2FileStorage.streamDocument("reservation-feed/devis.pdf");

            assertThat(result).isEqualTo(responseStream);
            var captor = org.mockito.ArgumentCaptor.forClass(GetObjectRequest.class);
            verify(s3Client).getObject(captor.capture());
            assertThat(captor.getValue().bucket()).isEqualTo(DOCUMENTS_BUCKET);
            assertThat(captor.getValue().key()).isEqualTo("reservation-feed/devis.pdf");
        }

        @Test
        void givenS3Failure_whenStreamDocument_thenThrowsIOException() {
            when(s3Client.getObject(any(GetObjectRequest.class)))
                    .thenThrow(S3Exception.builder().message("panne R2").build());

            assertThatThrownBy(() -> r2FileStorage.streamDocument("reservation-feed/devis.pdf"))
                    .isInstanceOf(IOException.class);
        }
    }
}
