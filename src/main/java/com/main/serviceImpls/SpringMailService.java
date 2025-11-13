package com.main.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.main.services.MailService;

@Service
public class SpringMailService implements MailService {

    @Autowired(required = false) private JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String from;

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            if (mailSender == null) {
                System.err.println("[MAIL] JavaMailSender not configured (spring-boot-starter-mail + spring.mail.*)");
                return;
            }
            SimpleMailMessage msg = new SimpleMailMessage();
            if (from != null && !from.isBlank()) {
                msg.setFrom(from);
            }
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("[MAIL] Failed to send: " + e.getMessage());
        }
    }
}
