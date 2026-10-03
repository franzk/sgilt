package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InitOnboardingRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    // -------------------------------------------------------------------------
    // Événement complet
    // -------------------------------------------------------------------------

    @Nested
    class Evenement {

        @Test
        void givenCompleteEvent_whenValidate_thenNoViolations() {
            assertThat(validator.validate(requestWith(evenement(new DemandeInitieeDto(UUID.randomUUID(), "Bonjour"))))).isEmpty();
        }

        @Test
        void givenNoEvent_whenValidate_thenReportsViolation() {
            Set<ConstraintViolation<InitOnboardingRequest>> violations = validator.validate(requestWith(null));

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("evenement");
        }

        @Test
        void givenDemandeWithoutPrestataire_whenValidate_thenReportsNestedViolation() {
            Set<ConstraintViolation<InitOnboardingRequest>> violations =
                    validator.validate(requestWith(evenement(new DemandeInitieeDto(null, "Bonjour"))));

            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .contains("evenement.rubriques[0].reservations[0].prestataireId");
        }

        @Test
        void givenDescriptionOverMaxLength_whenValidate_thenReportsNestedViolation() {
            EvenementDto evenement = new EvenementDto(
                    "mariage", null, null, "a".repeat(2001), LocalDate.of(2027, 6, 12), null, null, null, List.of());

            Set<ConstraintViolation<InitOnboardingRequest>> violations = validator.validate(requestWith(evenement));

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("evenement.description");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static EvenementDto evenement(DemandeInitieeDto demande) {
        return new EvenementDto("mariage", null, null, null, LocalDate.of(2027, 6, 12), "Lyon", null, null,
                List.of(new RubriqueDto("musique-animation", List.of(demande))));
    }

    private static InitOnboardingRequest requestWith(EvenementDto evenement) {
        return new InitOnboardingRequest("Jean", "Dupont", "jean.dupont@example.com", "06 12 34 56 78", evenement);
    }
}
