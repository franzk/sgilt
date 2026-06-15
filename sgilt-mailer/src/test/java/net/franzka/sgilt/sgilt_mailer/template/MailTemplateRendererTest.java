package net.franzka.sgilt.sgilt_mailer.template;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MailTemplateRendererTest {

    private final MailTemplateRenderer renderer = new MailTemplateRenderer();

    @Nested
    class Render {

        @Test
        void givenPlainValue_whenRender_thenPlaceholderReplaced() {
            String result = renderer.render("Bonjour {prenom}", Map.of("prenom", "Marie"), Set.of());

            assertThat(result).isEqualTo("Bonjour Marie");
        }

        @Test
        void givenValueWithHtmlSpecialChars_whenRender_thenValueIsEscaped() {
            String result = renderer.render("Message : {message}", Map.of("message", "<script>alert('x')</script> & co"), Set.of());

            assertThat(result).isEqualTo("Message : &lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt; &amp; co");
        }

        @Test
        void givenHtmlSafeValue_whenRender_thenValueIsNotEscaped() {
            String result = renderer.render("{content}", Map.of("content", "<strong>important</strong>"), Set.of("content"));

            assertThat(result).isEqualTo("<strong>important</strong>");
        }

        @Test
        void givenTemplateWithoutPlaceholders_whenRender_thenReturnedAsIs() {
            String result = renderer.render("Sgilt - Confirmez votre demande", Map.of(), Set.of());

            assertThat(result).isEqualTo("Sgilt - Confirmez votre demande");
        }

        @Test
        void givenMultiplePlaceholders_whenRender_thenAllReplaced() {
            String result = renderer.render("{prenom}, votre espace pour {prestataire} est prêt", Map.of("prenom", "Marie", "prestataire", "Studio Photo"), Set.of());

            assertThat(result).isEqualTo("Marie, votre espace pour Studio Photo est prêt");
        }
    }

    @Nested
    class Validate {

        @Test
        void givenAllRequiredVariablesPresent_whenValidate_thenNoException() {
            MailTemplate template = new MailTemplate("subject", "html", Set.of("appUrl"), Set.of());

            renderer.validate(MailType.WELCOME_EMAIL, template, Map.of("appUrl", "https://app.sgilt.fr"));
        }

        @Test
        void givenMissingRequiredVariable_whenValidate_thenThrowsMissingTemplateVariableException() {
            MailTemplate template = new MailTemplate("subject", "html", Set.of("appUrl"), Set.of());

            Map<String, Object> context = Map.of();

            assertThrows(MissingTemplateVariableException.class, () ->
                    renderer.validate(MailType.WELCOME_EMAIL, template, context));
        }

        @Test
        void givenRequiredVariableIsNull_whenValidate_thenThrowsMissingTemplateVariableException() {
            MailTemplate template = new MailTemplate("subject", "html", Set.of("appUrl"), Set.of());

            java.util.Map<String, Object> context = new java.util.HashMap<>();
            context.put("appUrl", null);

            assertThatThrownBy(() -> renderer.validate(MailType.WELCOME_EMAIL, template, context))
                    .isInstanceOf(MissingTemplateVariableException.class);
        }
    }
}
