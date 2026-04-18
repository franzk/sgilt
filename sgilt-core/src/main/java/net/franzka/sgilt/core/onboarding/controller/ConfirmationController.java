package net.franzka.sgilt.core.onboarding.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.onboarding.api.ConfirmationApi;
import net.franzka.sgilt.core.onboarding.domain.ConfirmationToken;
import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import net.franzka.sgilt.core.onboarding.service.ConfirmationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour la confirmation de la demande initiale par email
 */
@RestController
@RequiredArgsConstructor
public class ConfirmationController implements ConfirmationApi {

    private final ConfirmationService confirmationService;

    /**
     * Vérifie le token de confirmation reçu par email, le marque comme utilisé
     * et retourne un JWT set-password valide 5 minutes.
     * L'utilisateur aura 5 minutes entre le moment où il clique sur son lien de validation
     * et le moment où il saisit le mot de passe de son nouveau compte client.
     *
     * @param token le token de confirmation extrait du lien email
     * @return 200 OK avec l'email et le JWT set-password
     */
    @Override
    @Transactional
    public ResponseEntity<SetPasswordTokenDto> verifyToken(String token)
    {
        // 1 - validation le token et récupération de l'objet confirmationToken correspondant en BDD
        // et le passe en PENDING_CONFIRMATION
        ConfirmationToken confirmationToken = confirmationService.validateConfirmationToken(token);

        // 2 - générer un token pour la saisie d'un mot de passe pour la création de compte
        return ResponseEntity.ok(confirmationService.generateSetPasswordResponse(confirmationToken));
    }
}
