package net.franzka.sgilt.core.evenement.service;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.repository.JournalEvenementRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JournalEvenementServiceTest {

    @Mock private JournalEvenementRepository journalEvenementRepository;

    @InjectMocks
    private JournalEvenementService journalEvenementService;

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

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
}
