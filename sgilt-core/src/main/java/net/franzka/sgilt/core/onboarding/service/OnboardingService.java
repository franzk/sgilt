package net.franzka.sgilt.core.onboarding.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.confirmationtoken.domain.ConfirmationToken;
import net.franzka.sgilt.core.confirmationtoken.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.confirmationtoken.service.ConfirmationTokenJwtService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.api.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.api.dto.NewEvenementRequest;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final EvenementRepository evenementRepository;
    private final ReservationRepository reservationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenJwtService confirmationTokenJwtService;
    private final ConfirmationTokenProperties confirmationTokenProperties;

    @Transactional
    public DemandeInitialeResponse submitDemandeTunnel(NewEvenementRequest request) {

        Evenement evenement = Evenement.createDraft(request);
        evenementRepository.save(evenement);

        Reservation reservation = Reservation.createDraft(evenement, request.prestataireId());
        reservationRepository.save(reservation);

        String jwt = confirmationTokenJwtService.generateToken(
                request.email(),
                reservation.getId(),
                reservation.getCreatedAt()
        );
        String jti = confirmationTokenJwtService.extractJti(jwt);

        ConfirmationToken token = ConfirmationToken.create(
                jti,
                request.email(),
                reservation,
                confirmationTokenProperties.confirmationExpirationHours()
        );
        confirmationTokenRepository.save(token);

        // TODO: à retirer quand le mailer sera branché
        log.info("Confirmation JWT: {}", jwt);

        return new DemandeInitialeResponse(request.email());
    }
}
