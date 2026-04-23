package net.franzka.sgilt.sgilt_mailer.controller;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.mail.password=test",
                "spring.mail.username=noreply@sgilt.fr"
        }
)
class MailControllerIT {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private JavaMailSender mailSender;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
    }

    @Nested
    class PostMail {

        @Test
        void givenValidHtmlRequest_whenPostMail_thenReturns200() throws Exception {
            mockMvc.perform(post("/api/v1/mail")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "from": "sender@example.com",
                                      "to": "recipient@example.com",
                                      "subject": "Test",
                                      "html": "<p>Hello</p>"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Mail sent"));

            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        void givenValidTextRequest_whenPostMail_thenReturns200() throws Exception {
            mockMvc.perform(post("/api/v1/mail")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "from": "sender@example.com",
                                      "to": "recipient@example.com",
                                      "subject": "Test",
                                      "text": "Hello en texte"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Mail sent"));

            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                """
                { "to": "recipient@example.com", "subject": "Test", "text": "Hello" }
                """,
                """
                { "from": "sender@example.com", "subject": "Test", "text": "Hello" }
                """,
                """
                { "from": "sender@example.com", "to": "recipient@example.com", "text": "Hello" }
                """
        })
        void givenMissingMandatoryField_whenPostMail_thenReturns400(String body) throws Exception {
            mockMvc.perform(post("/api/v1/mail")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(mailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        void givenBlankFrom_whenPostMail_thenReturns400() throws Exception {
            mockMvc.perform(post("/api/v1/mail")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "from": "  ",
                                      "to": "recipient@example.com",
                                      "subject": "Test",
                                      "text": "Hello"
                                    }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void givenSmtpFailure_whenPostMail_thenReturns500() throws Exception {
            doThrow(new org.springframework.mail.MailSendException("SMTP down"))
                    .when(mailSender).send(any(MimeMessage.class));

            mockMvc.perform(post("/api/v1/mail")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "from": "sender@example.com",
                                      "to": "recipient@example.com",
                                      "subject": "Test",
                                      "text": "Hello"
                                    }
                                    """))
                    .andExpect(status().isInternalServerError());
        }
    }
}
