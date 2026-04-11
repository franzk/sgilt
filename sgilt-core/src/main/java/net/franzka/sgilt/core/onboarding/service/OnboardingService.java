package net.franzka.sgilt.core.onboarding.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.confirmationtoken.domain.ConfirmationToken;
import net.franzka.sgilt.core.confirmationtoken.repository.ConfirmationTokenRepository;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.repository.EvenementRepository;
import net.franzka.sgilt.core.onboarding.api.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.api.dto.NewEvenementRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.VerifyTokenResponse;
import net.franzka.sgilt.core.onboarding.exception.InvalidTokenException;
import net.franzka.sgilt.core.onboarding.exception.TokenAlreadyUsedException;
import net.franzka.sgilt.core.onboarding.exception.TokenExpiredException;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final EvenementRepository evenementRepository;
    private final ReservationRepository reservationRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenJwtService confirmationTokenJwtService;
    private final SetPasswordTokenJwtService setPasswordTokenJwtService;
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

    @Transactional
    public VerifyTokenResponse verifyToken(String token) {

        // 1. Vérifier expiration
        if (confirmationTokenJwtService.isExpired(token)) {
            throw new TokenExpiredException();
        }

        // 2. Extraire et valider les claims
        Claims claims;
        try {
            claims = confirmationTokenJwtService.extractClaims(token);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }

        // 3. Charger le ConfirmationToken via jti
        String jti = claims.getId();
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByJti(jti)
                .orElseThrow(EntityNotFoundException::new);

        // 4. Vérifier que le token n'est pas déjà consommé
        if (confirmationToken.isUsed()) {
            throw new TokenAlreadyUsedException();
        }

        // 5. Charger la réservation liée
        Reservation reservation = confirmationToken.getReservation();

        // 6. Croiser le claim reservationCreatedAt avec la BDD
        String claimedCreatedAt = claims.get("reservationCreatedAt", String.class);
        if (!reservation.getCreatedAt().toString().equals(claimedCreatedAt)) {
            throw new InvalidTokenException();
        }

        // 7. Marquer comme utilisé
        confirmationTokenRepository.markAsUsed(confirmationToken.getId());

        // 8. Supprimer le token
        confirmationTokenRepository.delete(confirmationToken);

        // 9. Générer le SetPasswordToken
        String email = claims.getSubject();
        String setPasswordToken = setPasswordTokenJwtService.generateToken(email, reservation.getId());

        // 10. Retourner la réponse
        return new VerifyTokenResponse(email, setPasswordToken);
    }

    @Transactional
    public ConfirmAccountResponse confirmAccount(ConfirmAccountRequest request) {
        String token = request.setPasswordToken();

        // 1. Vérifier expiration
        if (setPasswordTokenJwtService.isExpired(token)) {
            throw new TokenExpiredException();
        }

        // 2. Extraire et valider les claims
        Claims claims;
        try {
            claims = setPasswordTokenJwtService.extractClaims(token);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }

        // 3. Charger la réservation
        UUID reservationId = UUID.fromString(claims.get("reservationId", String.class));
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(EntityNotFoundException::new);

        // 4. Vérifier le statut DRAFT
        if (reservation.getStatus() != ReservationStatus.DRAFT) {
            throw new InvalidTokenException();
        }

        // 5. TODO: KC Admin API — créer user (email + password)
        // TODO: KC Admin API — set password (temporary=false)
        // TODO: KC — récupérer access + refresh token

        // 6. Passer la réservation à NOUVELLE
        reservationRepository.updateStatus(reservationId, ReservationStatus.NOUVELLE);

        // 7. TODO: notifier prestataire via sgilt-mailer

        // 8. Retourner les tokens (mockés)
        return new ConfirmAccountResponse(
                "TODO_KC_ACCESS_TOKEN",
                "TODO_KC_REFRESH_TOKEN"
        );
    }
}
