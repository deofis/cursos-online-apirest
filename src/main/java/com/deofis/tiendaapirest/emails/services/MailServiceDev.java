package com.deofis.tiendaapirest.emails.services;

import com.deofis.tiendaapirest.autenticacion.exceptions.MailSenderException;
import com.deofis.tiendaapirest.emails.dto.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 */
@Service
@AllArgsConstructor
@Profile({"dev", "devdocker"})
@Slf4j
public class MailServiceDev implements MailService {

    private final JavaMailSender javaMailSender;
    private final MailContentBuilder mailContentBuilder;

    @Override
    @Async
    public void sendEmail(NotificationEmail notificationEmail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("deofis-mailsender-167bea@inbox.mailtrap.io");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()) , true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Email sent!");
        } catch (MailException e) {
            throw new MailSenderException("Excepción al enviar email a: " + notificationEmail.getRecipient());
        }
    }

    @Override
    @Async
    public void sendEmail(NotificationEmail notificationEmail, String url) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("deofis-mailsender-167bea@inbox.mailtrap.io");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody(), url) , true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Email enviado!");
        } catch (MailException e) {
            throw new MailSenderException("Excepción al enviar email a: " + notificationEmail.getRecipient());
        }
    }

}
