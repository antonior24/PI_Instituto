package com.ies.poligono.sur.app.horario.service;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.PasswordRecoveryResult;
import com.ies.poligono.sur.app.horario.model.Usuario;
import java.security.SecureRandom;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PasswordRecoveryService {

  private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryService.class);
  private static final String EMAIL_SENT_MESSAGE =
      "Si el correo existe, se ha enviado una contrasena temporal al correo indicado.";
  private static final String TEMP_PASSWORD_MESSAGE =
      "Si el correo existe, se ha generado una contrasena temporal para pruebas.";
  private static final String PRODUCTION_MODE = "production";
  private static final String TESTING_MODE = "testing";
  private static final String SMTP_ERROR_MESSAGE =
      "No se pudo enviar el correo. Verifica Mailpit running (localhost:1025). ";
  private static final String PASSWORD_CHARSET =
      "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
  private static final int TEMP_PASSWORD_LENGTH = 8;

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final PasswordRecoveryEmailSender passwordRecoveryEmailSender;
  private final String passwordRecoveryMode;
  private final SecureRandom secureRandom;

  public PasswordRecoveryService(
      UsuarioRepository usuarioRepository,
      PasswordEncoder passwordEncoder,
      PasswordRecoveryEmailSender passwordRecoveryEmailSender,
      @Value("${app.password-recovery.mode:testing}") String passwordRecoveryMode) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.passwordRecoveryEmailSender = passwordRecoveryEmailSender;
    this.passwordRecoveryMode = normalizeMode(passwordRecoveryMode);
    this.secureRandom = new SecureRandom();
  }

  @Transactional
  public PasswordRecoveryResult recoverPassword(String email) {
    String mode = getEnvironmentName();
    boolean configuredSender = passwordRecoveryEmailSender.isConfigured();
    boolean testingMode = isTestingMode();

    logger.info(
        "Password recovery requested. mode={}, resendConfigured={}, email={}",
        mode,
        configuredSender,
        email);

    if (!testingMode && !configuredSender) {
      logger.error(
          "Password recovery cannot continue because production mode requires Resend"
              + " configuration");
      throw new IllegalStateException(
          "No se pudo enviar el correo. Verifica Mailpit (docker run -p 1025:1025 -p 8025:8025"
              + " mailpit/mailpit)");
    }

    if (!StringUtils.hasText(email)) {
      return new PasswordRecoveryResult(resolveMessage(testingMode), mode, null);
    }

    Usuario usuario = usuarioRepository.findByEmail(email.trim());
    if (usuario == null) {
      logger.info("Password recovery requested for non-existing email: {}", email);
      return new PasswordRecoveryResult(resolveMessage(testingMode), mode, null);
    }

    logger.info("Password recovery user found for email={}", usuario.getEmail());
    String temporaryPassword = generateTemporaryPassword();
    usuario.setPassword(passwordEncoder.encode(temporaryPassword));
    usuarioRepository.save(usuario);

    if (!testingMode) {
      logger.info(
          "Attempting to send password recovery email via SMTP/Mailpit to {}", usuario.getEmail());
      passwordRecoveryEmailSender.sendTemporaryPassword(usuario.getEmail(), temporaryPassword);
      logger.info("Password recovery email sent successfully to {}", usuario.getEmail());
      return new PasswordRecoveryResult(EMAIL_SENT_MESSAGE, mode, null);
    }

    return new PasswordRecoveryResult(TEMP_PASSWORD_MESSAGE, mode, temporaryPassword);
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
    return passwordRecoveryEmailSender.isConfigured();
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
}
