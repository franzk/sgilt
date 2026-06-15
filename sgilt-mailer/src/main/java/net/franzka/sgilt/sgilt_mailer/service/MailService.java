package net.franzka.sgilt.sgilt_mailer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.sgilt_mailer.dto.MailRequest;
import net.franzka.sgilt.sgilt_mailer.template.MailTemplate;
import net.franzka.sgilt.sgilt_mailer.template.MailTemplateRegistry;
import net.franzka.sgilt.sgilt_mailer.template.MailTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final MailTemplateRegistry templateRegistry;
    private final MailTemplateRenderer templateRenderer;

    @Value("${spring.mail.username}")
    private String defaultFromAddress;

    public void sendMail(MailRequest request) throws MessagingException, UnsupportedEncodingException {
        MailTemplate template = templateRegistry.getTemplate(request.getMailType());
        Map<String, Object> context = request.getContext() != null ? request.getContext() : Map.of();

        templateRenderer.validate(request.getMailType(), template, context);

        String subject = templateRenderer.render(template.subjectTemplate(), context, template.htmlSafeVariables());
        String html = templateRenderer.render(template.htmlTemplate(), context, template.htmlSafeVariables());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        InternetAddress from = new InternetAddress(defaultFromAddress, "Sgilt");

        helper.setFrom(from);
        helper.setTo(request.getTo());
        helper.setSubject(subject);
        helper.setText(html, true);

        log.info("Sending mail from {} to {} with mailType {} and subject {}", defaultFromAddress, request.getTo(), request.getMailType(), subject);
        mailSender.send(message);
        log.info("Mail sent successfully to {}", request.getTo());
    }

}
