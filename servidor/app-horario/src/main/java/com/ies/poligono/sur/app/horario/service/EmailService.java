package com.ies.poligono.sur.app.horario.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService implements PasswordRecoveryEmailSender {

  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username:no-reply@iespoligonosur.org}")
  private String defaultFrom;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public boolean isConfigured() {
    return mailSender != null && StringUtils.hasText(defaultFrom);
  }

  @Override
  public void sendTemporaryPassword(String destinatario, String temporaryPassword) {
    logger.info("Recovery email to {}", destinatario);
    enviarCorreo(
        destinatario,
        "Recuperación contraseña",
        "Tu contraseña temporal es: "
            + temporaryPassword
            + "\n\nInicia sesión y cámbiala.\n\nMailpit: localhost:8025");
  }

  public void enviarCorreo(String destinatario, String asunto, String contenido) {
    logger.info("Enviando SMTP a {}, asunto: {}", destinatario, asunto);
    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(destinatario);
    email.setFrom(defaultFrom);
    email.setSubject(asunto);
    email.setText(contenido);
    try {
      mailSender.send(email);
      logger.info("Email enviado OK a {}", destinatario);
    } catch (Exception e) {
      logger.error("SMTP FAIL a {}: {}", destinatario, e.getMessage());
      throw new RuntimeException("SMTP error", e);
    }
  }
}
