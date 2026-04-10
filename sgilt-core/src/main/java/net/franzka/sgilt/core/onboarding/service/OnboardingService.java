package net.franzka.sgilt.core.onboarding.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.confirmationtoken.domain.ConfirmationToken;
import net.franzka.sgilt.core.confirmationtoken.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.api.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.api.dto.NewEvenementRequest;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final EvenementRepository evenementRepository;
    private final ReservationRepository reservationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public DemandeInitialeResponse submitDemandeTunnel(NewEvenementRequest request) {

        Evenement evenement = Evenement.createDraft(request);
        evenementRepository.save(evenement);

        Reservation reservation = Reservation.createDraft(evenement, request.prestataireId());
        reservationRepository.save(reservation);

        ConfirmationToken token = ConfirmationToken.generate(request.email(), reservation);
        confirmationTokenRepository.save(token);

        return new DemandeInitialeResponse(request.email());
    }
}
