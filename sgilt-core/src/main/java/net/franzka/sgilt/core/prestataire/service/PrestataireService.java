package net.franzka.sgilt.core.prestataire.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service métier pour l'entité {@link Prestataire}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrestataireService {

    private final PrestataireRepository prestataireRepository;

    /**
     * Charge un prestataire par son identifiant.
     *
     * @param id l'identifiant du prestataire
     * @return le prestataire correspondant
     * @throws EntityNotFoundException si aucun prestataire ne correspond à cet identifiant
     */
    public Prestataire getById(UUID id) {
        Optional<Prestataire> optionalPrestataire = prestataireRepository.findById(id);
        if (optionalPrestataire.isEmpty()) {
            log.error("Prestataire with id {} not found", id);
            throw new EntityNotFoundException();
        } else {
            return optionalPrestataire.get();
        }
    }
}
