package net.franzka.sgilt.core.reservation.mapper;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Document;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.ReservationFeed;
import net.franzka.sgilt.core.reservation.dto.FeedFileDto;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationFeedMapperTest {

    private final ReservationFeedMapper mapper = new ReservationFeedMapper();

    private final UUID reservationId = UUID.randomUUID();

    private final Utilisateur client = Utilisateur.builder()
            .id(UUID.randomUUID()).firstName("Sophie").lastName("Leroy").avatarUrl("sophie.jpg").build();

    private final Utilisateur prestataireUtilisateur = Utilisateur.builder().id(UUID.randomUUID()).build();
    private final Prestataire prestataire = Prestataire.builder()
            .utilisateur(prestataireUtilisateur).name("Studio Fleur").avatar("studio.jpg").build();

    // -------------------------------------------------------------------------
    // toFeedItem — Note
    // -------------------------------------------------------------------------

    @Nested
    class ToFeedItemNote {

        @Test
        void givenNoteFromClient_whenToFeedItem_thenMapsClientAuthorFields() {
            LocalDateTime createdAt = LocalDateTime.of(2027, 1, 1, 10, 0);
            Note note = Note.builder()
                    .id(UUID.randomUUID()).utilisateur(client).createdAt(createdAt)
                    .title("Titre").content("Contenu").isPersonal(false).build();

            FeedItemDto dto = mapper.toFeedItem(note, true, reservationId);

            assertThat(dto.type()).isEqualTo("note");
            assertThat(dto.id()).isEqualTo(note.getId());
            assertThat(dto.authorId()).isEqualTo(client.getId().toString());
            assertThat(dto.authorName()).isEqualTo("Sophie Leroy");
            assertThat(dto.authorPhoto()).isEqualTo("sophie.jpg");
            assertThat(dto.authorRole()).isEqualTo("client");
            assertThat(dto.createdAt()).isEqualTo(createdAt);
            assertThat(dto.title()).isEqualTo("Titre");
            assertThat(dto.content()).isEqualTo("Contenu");
            assertThat(dto.isPersonal()).isFalse();
            assertThat(dto.isMessageInitial()).isTrue();
            assertThat(dto.generatedKey()).isNull();
        }

        @Test
        void givenNoteFromPrestataire_whenToFeedItem_thenMapsPrestataireAuthorFields() {
            Note note = Note.builder()
                    .id(UUID.randomUUID()).prestataire(prestataire).createdAt(LocalDateTime.now())
                    .generatedKey("feed.system.confirmed").isPersonal(false).build();

            FeedItemDto dto = mapper.toFeedItem(note, false, reservationId);

            assertThat(dto.authorId()).isEqualTo(prestataireUtilisateur.getId().toString());
            assertThat(dto.authorName()).isEqualTo("Studio Fleur");
            assertThat(dto.authorPhoto()).isEqualTo("studio.jpg");
            assertThat(dto.authorRole()).isEqualTo("prestataire");
            assertThat(dto.generatedKey()).isEqualTo("feed.system.confirmed");
            assertThat(dto.isMessageInitial()).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // toFeedItem — Document
    // -------------------------------------------------------------------------

    @Nested
    class ToFeedItemDocument {

        @Test
        void givenDocumentWithoutUploadId_whenToFeedItem_thenMapsSingleFileUploadWithDocumentId() {
            UUID documentId = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.of(2027, 1, 1, 10, 0);
            Document document = Document.builder()
                    .id(documentId).utilisateur(client).createdAt(createdAt)
                    .title("devis.pdf").fileName("devis.pdf").mimeType("application/pdf")
                    .filePath("path/devis.pdf").isPersonal(true).build();

            FeedItemDto dto = mapper.toFeedItem(document, false, reservationId);

            assertThat(dto.type()).isEqualTo("document");
            assertThat(dto.id()).isEqualTo(documentId);
            assertThat(dto.authorId()).isEqualTo(client.getId().toString());
            assertThat(dto.authorRole()).isEqualTo("client");
            assertThat(dto.title()).isEqualTo("devis.pdf");
            assertThat(dto.files()).containsExactly(new FeedFileDto(
                    documentId, "devis.pdf", "application/pdf",
                    "/reservations/" + reservationId + "/feed/documents/" + documentId));
            assertThat(dto.isPersonal()).isTrue();
            assertThat(dto.content()).isNull();
        }

        @Test
        void givenDocumentWithUploadId_whenToFeedItem_thenUsesUploadIdAsItemId() {
            UUID uploadId = UUID.randomUUID();
            Document document = Document.builder()
                    .id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now())
                    .fileName("devis.pdf").filePath("path/devis.pdf").uploadId(uploadId).isPersonal(false).build();

            FeedItemDto dto = mapper.toFeedItem(document, false, reservationId);

            assertThat(dto.id()).isEqualTo(uploadId);
            assertThat(dto.files()).extracting(FeedFileDto::id).containsExactly(document.getId());
        }

        @Test
        void givenDocumentFromPrestataire_whenToFeedItem_thenMapsPrestataireAuthorFields() {
            Document document = Document.builder()
                    .id(UUID.randomUUID()).prestataire(prestataire).createdAt(LocalDateTime.now())
                    .fileName("contrat.pdf").filePath("path/contrat.pdf").isPersonal(false).build();

            FeedItemDto dto = mapper.toFeedItem(document, false, reservationId);

            assertThat(dto.authorId()).isEqualTo(prestataireUtilisateur.getId().toString());
            assertThat(dto.authorName()).isEqualTo("Studio Fleur");
            assertThat(dto.authorRole()).isEqualTo("prestataire");
        }
    }

    // -------------------------------------------------------------------------
    // toFeedItem — type inconnu
    // -------------------------------------------------------------------------

    @Nested
    class ToFeedItemUnknownType {

        @Test
        void givenUnknownFeedItemSubtype_whenToFeedItem_thenThrowsIllegalArgumentException() {
            ReservationFeed unknown = mock(ReservationFeed.class);
            when(unknown.getUtilisateur()).thenReturn(client);

            assertThatThrownBy(() -> mapper.toFeedItem(unknown, false, reservationId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // toFeedItems
    // -------------------------------------------------------------------------

    @Nested
    class ToFeedItems {

        @Test
        void givenEmptyList_whenToFeedItems_thenReturnsEmptyList() {
            assertThat(mapper.toFeedItems(List.of(), reservationId)).isEmpty();
        }

        @Test
        void givenMultipleItems_whenToFeedItems_thenOnlyFirstIsMarkedAsMessageInitial() {
            Note first = Note.builder().id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now()).isPersonal(false).build();
            Note second = Note.builder().id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now()).isPersonal(false).build();

            List<FeedItemDto> result = mapper.toFeedItems(List.of(first, second), reservationId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).isMessageInitial()).isTrue();
            assertThat(result.get(1).isMessageInitial()).isFalse();
        }

        @Test
        void givenDocumentsOfSameUpload_whenToFeedItems_thenGroupsThemInOneItemAtFirstDocumentPosition() {
            UUID uploadId = UUID.randomUUID();
            Note initial = Note.builder().id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now()).isPersonal(false).build();
            Document devis = document(uploadId, "devis.pdf");
            Note note = Note.builder().id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now()).isPersonal(false).build();
            Document contrat = document(uploadId, "contrat.pdf");

            List<FeedItemDto> result = mapper.toFeedItems(List.of(initial, devis, note, contrat), reservationId);

            assertThat(result).extracting(FeedItemDto::id).containsExactly(initial.getId(), uploadId, note.getId());
            assertThat(result.get(1).files()).extracting(FeedFileDto::name).containsExactly("devis.pdf", "contrat.pdf");
        }

        @Test
        void givenDocumentsWithoutUploadId_whenToFeedItems_thenEachIsItsOwnItem() {
            Note initial = Note.builder().id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now()).isPersonal(false).build();
            Document devis = document(null, "devis.pdf");
            Document contrat = document(null, "contrat.pdf");

            List<FeedItemDto> result = mapper.toFeedItems(List.of(initial, devis, contrat), reservationId);

            assertThat(result).extracting(FeedItemDto::id).containsExactly(initial.getId(), devis.getId(), contrat.getId());
            assertThat(result.get(1).files()).hasSize(1);
            assertThat(result.get(2).files()).hasSize(1);
        }

        private Document document(UUID uploadId, String fileName) {
            return Document.builder()
                    .id(UUID.randomUUID()).utilisateur(client).createdAt(LocalDateTime.now())
                    .fileName(fileName).filePath("path/" + fileName).uploadId(uploadId).isPersonal(false).build();
        }
    }
}
