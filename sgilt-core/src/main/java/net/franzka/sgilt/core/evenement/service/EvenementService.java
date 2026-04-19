package net.franzka.sgilt.core.evenement.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
     * @param firstName   le prénom du demandeur
     * @param lastName    le nom du demandeur
     * @param email       l'adresse email du demandeur
     * @param eventType   le type d'événement (nullable)
     * @param ambiance    l'ambiance souhaitée (nullable)
     * @param momentCle   le moment clé de l'événement (nullable)
     * @param description la description libre de l'événement (nullable)
     * @param date        la date de l'événement (nullable)
     * @param ville       la ville de l'événement (nullable)
     * @param nbInvites   le nombre d'invités (nullable)
     * @param lieu        le lieu de l'événement (nullable)
     * @param telephone   le numéro de téléphone du demandeur (nullable)
     * @return l'événement sauvegardé
     */
    public Evenement createDraft(
            String firstName, String lastName, String email,
            String eventType, String ambiance, String momentCle,
            String description, LocalDate date, String ville,
            String nbInvites, String lieu, String telephone) {
        Evenement evenement = Evenement.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .status(EvenementStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .eventType(eventType)
                .ambiance(ambiance)
                .momentCle(momentCle)
                .description(description)
                .date(date)
                .ville(ville)
                .nbInvites(nbInvites)
                .lieu(lieu)
                .telephone(telephone)
                .build();
        return evenementRepository.save(evenement);
    }
}
