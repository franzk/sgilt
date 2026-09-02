package net.franzka.sgilt.core.pro.controller;

import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.pro.dto.ProMeDto;
import net.franzka.sgilt.core.pro.service.ProProvisioningService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProControllerTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProProvisioningService proProvisioningService;

    @Mock
    private PrestataireService prestataireService;

    @InjectMocks
    private ProController controller;

    @Nested
    class GetMe {

        @Test
        void givenLinkedPrestataire_whenGetMe_thenProvisionsThenReturnsProfileWithSlug() {
            UUID id = UUID.randomUUID();
            Utilisateur utilisateur = Utilisateur.builder()
                    .id(id).email("pro@sgilt.fr").firstName("Jean").lastName("Dupont").build();
            when(currentUserService.get()).thenReturn(utilisateur);
            when(prestataireService.getSlugByUtilisateur(utilisateur)).thenReturn("studio-fleur");

            ResponseEntity<ProMeDto> result = controller.getMe();

            assertThat(result.getBody()).isEqualTo(new ProMeDto(id, "pro@sgilt.fr", "Jean", "Dupont", "studio-fleur"));
            InOrder order = inOrder(proProvisioningService, currentUserService);
            order.verify(proProvisioningService).provisionIfNeeded();
            order.verify(currentUserService).get();
        }

        @Test
        void givenNoLinkedPrestataire_whenGetMe_thenSlugIsNull() {
            UUID id = UUID.randomUUID();
            Utilisateur utilisateur = Utilisateur.builder()
                    .id(id).email("pro@sgilt.fr").firstName("Jean").lastName("Dupont").build();
            when(currentUserService.get()).thenReturn(utilisateur);
            when(prestataireService.getSlugByUtilisateur(utilisateur)).thenReturn(null);

            ResponseEntity<ProMeDto> result = controller.getMe();

            assertThat(result.getBody().slug()).isNull();
        }
    }
}
