package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import net.franzka.sgilt.core.evenement.dto.JournalEvenementDto;
import net.franzka.sgilt.core.evenement.dto.ModificationChamp;
import net.franzka.sgilt.core.evenement.mapper.JournalEvenementMapper;
import net.franzka.sgilt.core.evenement.repository.JournalEvenementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service métier pour la journalisation des modifications des métadonnées d'un événement.
 */
@Service
@RequiredArgsConstructor
public class JournalEvenementService {

    private static final int PAGE_SIZE = 20;

    private final JournalEvenementRepository journalEvenementRepository;
    private final JournalEvenementMapper     journalEvenementMapper;

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

    /**
     * Retourne la date de la dernière modification journalisée pour un événement.
     *
     * @param evenementId l'identifiant de l'événement
     * @return la date de la dernière modification, ou vide si aucune entrée
     */
    public Optional<LocalDateTime> derniereModification(UUID evenementId) {
        return journalEvenementRepository.findFirstByEvenementIdOrderByCreatedAtDesc(evenementId)
                .map(JournalEvenement::getCreatedAt);
    }

    /**
     * Retourne une page d'entrées de journal pour un événement, de la plus récente à la plus ancienne.
     *
     * @param evenementId l'identifiant de l'événement
     * @param page        le numéro de page (0-indexé)
     * @return une page de {@link JournalEvenementDto}
     */
    public Page<JournalEvenementDto> getPage(UUID evenementId, int page) {
        return journalEvenementRepository
                .findByEvenementIdOrderByCreatedAtDesc(evenementId, PageRequest.of(page, PAGE_SIZE))
                .map(journalEvenementMapper::toDto);
    }
}
