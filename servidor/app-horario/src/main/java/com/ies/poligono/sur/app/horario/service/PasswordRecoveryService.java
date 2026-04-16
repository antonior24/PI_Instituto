package com.ies.poligono.sur.app.horario.service;

import java.security.SecureRandom;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.PasswordRecoveryResult;
import com.ies.poligono.sur.app.horario.model.Usuario;

@Service
public class PasswordRecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryService.class);
    private static final String PROD_MESSAGE = "Si el correo existe, se ha enviado una contrasena temporal al correo indicado.";
    private static final String NON_PROD_MESSAGE = "Si el correo existe, se ha generado una contrasena temporal para pruebas.";
    private static final String PASSWORD_CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 8;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final JavaMailSender mailSender;
    private final boolean exposeTemporaryPassword;
    private final String mailFrom;
    private final SecureRandom secureRandom;

    public PasswordRecoveryService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            Environment environment,
            @Nullable JavaMailSender mailSender,
            @Value("${app.password-recovery.expose-temporary-password:true}") boolean exposeTemporaryPassword,
            @Value("${app.password-recovery.mail.from:${spring.mail.username:no-reply@app-horario.local}}") String mailFrom) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
        this.mailSender = mailSender;
        this.exposeTemporaryPassword = exposeTemporaryPassword;
        this.mailFrom = mailFrom;
        this.secureRandom = new SecureRandom();
    }

    public PasswordRecoveryResult recoverPassword(String email) {
        boolean production = isProduction();
        String message = production ? PROD_MESSAGE : NON_PROD_MESSAGE;

        if (!StringUtils.hasText(email)) {
            return new PasswordRecoveryResult(message, null);
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim());
        if (usuario == null) {
            logger.info("Password recovery requested for non-existing email: {}", email);
            return new PasswordRecoveryResult(message, null);
        }

        String temporaryPassword = generateTemporaryPassword();
        usuario.setPassword(passwordEncoder.encode(temporaryPassword));
        usuarioRepository.save(usuario);

        if (production) {
            sendTemporaryPasswordEmail(usuario.getEmail(), temporaryPassword);
            return new PasswordRecoveryResult(message, null);
        }

        String exposedPassword = exposeTemporaryPassword ? temporaryPassword : null;
        return new PasswordRecoveryResult(message, exposedPassword);
    }

    String generateTemporaryPassword() {
        StringBuilder temporaryPassword = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(PASSWORD_CHARSET.length());
            temporaryPassword.append(PASSWORD_CHARSET.charAt(index));
        }
        return temporaryPassword.toString();
    }

    private boolean isProduction() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> "prod".equalsIgnoreCase(profile));
    }

    private void sendTemporaryPasswordEmail(String recipient, String temporaryPassword) {
        if (mailSender == null) {
            throw new IllegalStateException("No hay un JavaMailSender configurado para el perfil de produccion");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setFrom(mailFrom);
        message.setSubject("Recuperacion de contrasena temporal");
        message.setText(buildEmailBody(temporaryPassword));
        mailSender.send(message);
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
