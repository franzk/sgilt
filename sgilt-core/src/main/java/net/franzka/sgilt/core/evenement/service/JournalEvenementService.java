package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.repository.JournalEvenementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier pour la journalisation des modifications des métadonnées d'un événement.
 */
@Service
@RequiredArgsConstructor
public class JournalEvenementService {

    private final JournalEvenementRepository journalEvenementRepository;

    /**
     * Persiste une entrée de journal si des modifications ont été détectées.
     * Sans effet si la liste est vide.
     *
     * @param evenement     l'événement modifié
     * @param modifications la liste des champs modifiés (champ, avant, après)
     */
    public void save(Evenement evenement, List<ModificationChamp> modifications) {
        if (modifications.isEmpty()) return;
        journalEvenementRepository.save(
                JournalEvenement.builder()
                        .evenement(evenement)
                        .modifications(modifications)
                        .build()
        );
    }
}
