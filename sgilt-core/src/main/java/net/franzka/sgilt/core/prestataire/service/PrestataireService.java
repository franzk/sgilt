package net.franzka.sgilt.core.prestataire.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service métier pour l'entité {@link Prestataire}.
 */
@Service
@RequiredArgsConstructor
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
        return prestataireRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
