package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service métier pour l'entité {@link Evenement}.
 */
@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;

    /**
     * Crée et persiste un événement en statut ACTIVE pour l'utilisateur donné.
     *
     * @param utilisateur l'utilisateur propriétaire de l'événement
     * @param eventType   le type d'événement utilisé comme nom (nullable — valeur par défaut si absent)
     * @param date        la date de l'événement (nullable)
     * @return l'événement sauvegardé
     */
    public Evenement create(Utilisateur utilisateur, String eventType, LocalDate date) {
        Evenement evenement = Evenement.builder()
                .utilisateur(utilisateur)
                .name(eventType != null ? eventType : "Mon événement")
                .date(date)
                .statut(EvenementStatus.ACTIVE)
                .build();
        return evenementRepository.save(evenement);
    }
}
