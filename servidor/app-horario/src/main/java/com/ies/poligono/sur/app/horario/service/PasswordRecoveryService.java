package com.ies.poligono.sur.app.horario.service;

import java.security.SecureRandom;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.PasswordRecoveryResult;
import com.ies.poligono.sur.app.horario.model.Usuario;

@Service
public class PasswordRecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryService.class);
    private static final String EMAIL_SENT_MESSAGE = "Si el correo existe, se ha enviado una contrasena temporal al correo indicado.";
    private static final String TEMP_PASSWORD_MESSAGE = "Si el correo existe, se ha generado una contrasena temporal para pruebas.";
    private static final String PRODUCTION_MODE = "production";
    private static final String TESTING_MODE = "testing";
    private static final String MAIL_ERROR_MESSAGE = "No se pudo enviar el correo de recuperacion. Revisa la configuracion SMTP del entorno.";
    private static final String PASSWORD_CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 8;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String passwordRecoveryMode;
    private final String mailFrom;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;
    private final SecureRandom secureRandom;

    public PasswordRecoveryService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Nullable JavaMailSender mailSender,
            @Value("${app.password-recovery.mode:testing}") String passwordRecoveryMode,
            @Value("${app.password-recovery.mail.from:${spring.mail.username:no-reply@app-horario.local}}") String mailFrom,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.passwordRecoveryMode = normalizeMode(passwordRecoveryMode);
        this.mailFrom = mailFrom;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.secureRandom = new SecureRandom();
    }

    @Transactional
    public PasswordRecoveryResult recoverPassword(String email) {
        String environmentName = getEnvironmentName();
        boolean mailConfigured = isMailConfigured();
        boolean testingMode = isTestingMode();

        logger.info("Password recovery requested. mode={}, mailConfigured={}, email={}",
                environmentName, mailConfigured, email);

        if (!testingMode && !mailConfigured) {
            logger.error("Password recovery cannot continue because production mode requires SMTP configuration");
            throw new IllegalStateException(MAIL_ERROR_MESSAGE);
        }

        if (!StringUtils.hasText(email)) {
            return new PasswordRecoveryResult(resolveMessage(testingMode), environmentName, null);
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim());
        if (usuario == null) {
            logger.info("Password recovery requested for non-existing email: {}", email);
            return new PasswordRecoveryResult(resolveMessage(testingMode), environmentName, null);
        }

        logger.info("Password recovery user found for email={}", usuario.getEmail());
        String temporaryPassword = generateTemporaryPassword();
        usuario.setPassword(passwordEncoder.encode(temporaryPassword));
        usuarioRepository.save(usuario);

        if (!testingMode) {
            sendTemporaryPasswordEmail(usuario.getEmail(), temporaryPassword);
            return new PasswordRecoveryResult(EMAIL_SENT_MESSAGE, environmentName, null);
        }

        return new PasswordRecoveryResult(TEMP_PASSWORD_MESSAGE, environmentName, temporaryPassword);
    }

    String generateTemporaryPassword() {
        StringBuilder temporaryPassword = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(PASSWORD_CHARSET.length());
            temporaryPassword.append(PASSWORD_CHARSET.charAt(index));
        }
        return temporaryPassword.toString();
    }

    public String getEnvironmentName() {
        return passwordRecoveryMode;
    }

    public boolean isMailConfigured() {
        return mailSender != null
                && StringUtils.hasText(mailHost)
                && StringUtils.hasText(mailUsername)
                && StringUtils.hasText(mailPassword)
                && StringUtils.hasText(mailFrom);
    }

    private boolean isTestingMode() {
        return TESTING_MODE.equals(passwordRecoveryMode);
    }

    private String resolveMessage(boolean testingMode) {
        return testingMode ? TEMP_PASSWORD_MESSAGE : EMAIL_SENT_MESSAGE;
    }

    private String normalizeMode(String configuredMode) {
        if (!StringUtils.hasText(configuredMode)) {
            return TESTING_MODE;
        }

        String normalizedMode = configuredMode.trim().toLowerCase(Locale.ROOT);
        if (PRODUCTION_MODE.equals(normalizedMode)) {
            return PRODUCTION_MODE;
        }

        return TESTING_MODE;
    }

    private void sendTemporaryPasswordEmail(String recipient, String temporaryPassword) {
        if (!isMailConfigured()) {
            logger.error("Password recovery email could not be sent because SMTP is not fully configured");
            throw new IllegalStateException(MAIL_ERROR_MESSAGE);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setFrom(mailFrom);
        message.setSubject("Recuperacion de contrasena temporal");
        message.setText(buildEmailBody(temporaryPassword));
        logger.info("Attempting to send password recovery email to {}", recipient);
        try {
            mailSender.send(message);
            logger.info("Password recovery email sent successfully to {}", recipient);
        } catch (MailException e) {
            logger.error("Password recovery email failed for {}", recipient, e);
            throw new IllegalStateException(MAIL_ERROR_MESSAGE, e);
        }
    }

    private String buildEmailBody(String temporaryPassword) {
        return """
                Hola,

                Se ha solicitado la recuperacion de tu cuenta en app-horario.

                Tu contrasena temporal es: %s

                Usa esta contrasena para iniciar sesion y cambiala lo antes posible.

                Si no has solicitado este cambio, contacta con administracion.
                """.formatted(temporaryPassword);
    }
}
