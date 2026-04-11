package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service métier pour l'entité {@link Evenement}.
 */
@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;

    /**
     * Crée et persiste un événement en statut DRAFT.
     *
     * @param firstName le prénom du demandeur
     * @param lastName  le nom du demandeur
     * @param email     l'adresse email du demandeur
     * @return l'événement sauvegardé
     */
    public Evenement createDraft(String firstName, String lastName, String email) {
        Evenement evenement = Evenement.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .status(EvenementStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();
        return evenementRepository.save(evenement);
    }
}
