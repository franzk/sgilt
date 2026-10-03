package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InitOnboardingDemandeRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static final UUID PRESTATAIRE_ID = UUID.randomUUID();

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
    // Champs texte libre bornés en longueur
    // -------------------------------------------------------------------------

    @Nested
    class FreeTextFieldBounds {

        @Test
        void givenAllFieldsWithinBounds_whenValidate_thenNoViolations() {
            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(validRequest());

            assertThat(violations).isEmpty();
        }

        @Test
        void givenDescriptionOverMaxLength_whenValidate_thenReportsViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withDescription(r, "a".repeat(2001)));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("description");
        }

        @Test
        void givenVilleOverMaxLength_whenValidate_thenReportsViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withVille(r, "a".repeat(101)));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("ville");
        }

        @Test
        void givenPrestataireMessageOverMaxLength_whenValidate_thenReportsViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withPrestataireMessage(r, "a".repeat(1001)));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("prestataireMessage");
        }
    }

    // -------------------------------------------------------------------------
    // telephone
    // -------------------------------------------------------------------------

    @Nested
    class Telephone {

        @Test
        void givenNullTelephone_whenValidate_thenNoViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withTelephone(r, null));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).doesNotContain("telephone");
        }

        @Test
        void givenFrenchMobileWithSpaces_whenValidate_thenNoViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withTelephone(r, "06 12 34 56 78"));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).doesNotContain("telephone");
        }

        @Test
        void givenInternationalFormat_whenValidate_thenNoViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withTelephone(r, "+33 6 12 34 56 78"));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).doesNotContain("telephone");
        }

        @Test
        void givenTooFewDigits_whenValidate_thenReportsViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withTelephone(r, "123456"));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("telephone");
        }

        @Test
        void givenLetters_whenValidate_thenReportsViolation() {
            InitOnboardingDemandeRequest request = requestWith(r -> withTelephone(r, "not-a-phone-number"));

            Set<ConstraintViolation<InitOnboardingDemandeRequest>> violations = validator.validate(request);

            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("telephone");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private InitOnboardingDemandeRequest validRequest() {
        return new InitOnboardingDemandeRequest(
                "Jean", "Dupont", "jean.dupont@example.com", PRESTATAIRE_ID,
                "anniversaire", "festif", "danse", "Une belle fête", LocalDate.of(2026, 6, 15),
                "Strasbourg", "50", "Salle des fêtes", "06 12 34 56 78", "Merci de me recontacter vite");
    }

    private InitOnboardingDemandeRequest requestWith(java.util.function.Function<InitOnboardingDemandeRequest, InitOnboardingDemandeRequest> mutation) {
        return mutation.apply(validRequest());
    }

    private InitOnboardingDemandeRequest withDescription(InitOnboardingDemandeRequest r, String description) {
        return new InitOnboardingDemandeRequest(
                r.firstName(), r.lastName(), r.email(), r.prestataireId(),
                r.eventType(), r.ambiance(), r.momentCle(), description, r.date(),
                r.ville(), r.nbInvites(), r.lieu(), r.telephone(), r.prestataireMessage());
    }

    private InitOnboardingDemandeRequest withVille(InitOnboardingDemandeRequest r, String ville) {
        return new InitOnboardingDemandeRequest(
                r.firstName(), r.lastName(), r.email(), r.prestataireId(),
                r.eventType(), r.ambiance(), r.momentCle(), r.description(), r.date(),
                ville, r.nbInvites(), r.lieu(), r.telephone(), r.prestataireMessage());
    }

    private InitOnboardingDemandeRequest withPrestataireMessage(InitOnboardingDemandeRequest r, String prestataireMessage) {
        return new InitOnboardingDemandeRequest(
                r.firstName(), r.lastName(), r.email(), r.prestataireId(),
                r.eventType(), r.ambiance(), r.momentCle(), r.description(), r.date(),
                r.ville(), r.nbInvites(), r.lieu(), r.telephone(), prestataireMessage);
    }

    private InitOnboardingDemandeRequest withTelephone(InitOnboardingDemandeRequest r, String telephone) {
        return new InitOnboardingDemandeRequest(
                r.firstName(), r.lastName(), r.email(), r.prestataireId(),
                r.eventType(), r.ambiance(), r.momentCle(), r.description(), r.date(),
                r.ville(), r.nbInvites(), r.lieu(), telephone, r.prestataireMessage());
    }
}
