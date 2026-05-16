package net.franzka.sgilt.core.evenement.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import net.franzka.sgilt.core.evenement.dto.JournalEvenementDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.mapper.JournalEvenementMapper;
import net.franzka.sgilt.core.evenement.repository.JournalEvenementRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalEvenementServiceTest {

    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock private JournalEvenementRepository journalEvenementRepository;
    @Mock private JournalEvenementMapper     journalEvenementMapper;

    @InjectMocks
    private JournalEvenementService journalEvenementService;

    // ── Save ─────────────────────────────────────────────────────────────────────

    @Nested
    class Save {

        @Test
        void givenEmptyModifications_whenSave_thenNothingPersisted() {
            journalEvenementService.save(Evenement.builder().build(), List.of());

            verify(journalEvenementRepository, never()).save(any(JournalEvenement.class));
        }

        @Test
        void givenOneModification_whenSave_thenJournalEntryPersisted() {
            Evenement event = Evenement.builder().build();
            List<ModificationChamp> modifications = List.of(new ModificationChamp("lieu", "Paris", "Lyon"));

            journalEvenementService.save(event, modifications);

            ArgumentCaptor<JournalEvenement> captor = ArgumentCaptor.forClass(JournalEvenement.class);
            verify(journalEvenementRepository).save(captor.capture());
            JournalEvenement saved = captor.getValue();
            assertThat(saved.getEvenement()).isEqualTo(event);
            assertThat(saved.getModifications()).containsExactly(new ModificationChamp("lieu", "Paris", "Lyon"));
        }

        @Test
        void givenMultipleModifications_whenSave_thenAllModificationsPersisted() {
            Evenement event = Evenement.builder().build();
            List<ModificationChamp> modifications = List.of(
                    new ModificationChamp("lieu", "Niedermorschwir", "Barr"),
                    new ModificationChamp("ville", "Schlettstadt", "Rathsamausen")
            );

            journalEvenementService.save(event, modifications);

            ArgumentCaptor<JournalEvenement> captor = ArgumentCaptor.forClass(JournalEvenement.class);
            verify(journalEvenementRepository).save(captor.capture());
            assertThat(captor.getValue().getModifications()).containsExactlyInAnyOrderElementsOf(modifications);
        }
    }

    // ── DerniereModification ─────────────────────────────────────────────────────

    @Nested
    class DerniereModification {

        @Test
        void givenNoJournalEntry_whenDerniereModification_thenReturnsEmpty() {
            when(journalEvenementRepository.findFirstByEvenementIdOrderByCreatedAtDesc(EVENT_ID))
                    .thenReturn(Optional.empty());

            assertThat(journalEvenementService.derniereModification(EVENT_ID)).isEmpty();
        }

        @Test
        void givenJournalEntry_whenDerniereModification_thenReturnsCreatedAt() {
            LocalDateTime createdAt = LocalDateTime.of(2026, 5, 12, 14, 30);
            JournalEvenement entry = JournalEvenement.builder().build();
            entry.setCreatedAt(createdAt);

            when(journalEvenementRepository.findFirstByEvenementIdOrderByCreatedAtDesc(EVENT_ID))
                    .thenReturn(Optional.of(entry));

            assertThat(journalEvenementService.derniereModification(EVENT_ID)).contains(createdAt);
        }
    }

    // ── GetPage ───────────────────────────────────────────────────────────────────

    @Nested
    class GetPage {

        @Test
        void givenJournalEntries_whenGetPage_thenReturnsMappedDtoPage() {
            JournalEvenement entry = JournalEvenement.builder().build();
            JournalEvenementDto dto = new JournalEvenementDto(UUID.randomUUID(), LocalDateTime.now(), List.of());

            when(journalEvenementRepository.findByEvenementIdOrderByCreatedAtDesc(eq(EVENT_ID), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(entry)));
            when(journalEvenementMapper.toDto(entry)).thenReturn(dto);

            Page<JournalEvenementDto> result = journalEvenementService.getPage(EVENT_ID, 0);

            assertThat(result.getContent()).containsExactly(dto);
        }

        @Test
        void givenNoEntries_whenGetPage_thenReturnsEmptyPage() {
            when(journalEvenementRepository.findByEvenementIdOrderByCreatedAtDesc(eq(EVENT_ID), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            Page<JournalEvenementDto> result = journalEvenementService.getPage(EVENT_ID, 0);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.isLast()).isTrue();
        }

        @Test
        void givenPageRequested_whenGetPage_thenPageableHasCorrectPageNumber() {
            when(journalEvenementRepository.findByEvenementIdOrderByCreatedAtDesc(eq(EVENT_ID), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            journalEvenementService.getPage(EVENT_ID, 2);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(journalEvenementRepository).findByEvenementIdOrderByCreatedAtDesc(eq(EVENT_ID), captor.capture());
            assertThat(captor.getValue().getPageNumber()).isEqualTo(2);
            assertThat(captor.getValue().getPageSize()).isEqualTo(20);
        }
    }
}
