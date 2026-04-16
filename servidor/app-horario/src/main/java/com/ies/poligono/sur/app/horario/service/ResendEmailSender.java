package com.ies.poligono.sur.app.horario.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ResendEmailSender implements PasswordRecoveryEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(ResendEmailSender.class);
    private static final String RESEND_BASE_URL = "https://api.resend.com";
    private static final String RESEND_ERROR_MESSAGE = "No se pudo enviar el correo de recuperacion. Revisa la configuracion de Resend.";

    private final String apiKey;
    private final String fromAddress;
    private final String replyTo;
    private WebClient webClient;

    public ResendEmailSender(
            @Value("${resend.api.key:}") String apiKey,
            @Value("${resend.from:}") String fromAddress,
            @Value("${resend.reply-to:}") String replyTo) {
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
        this.replyTo = replyTo;
    }

    @Override
    public boolean isConfigured() {
        return StringUtils.hasText(apiKey) && StringUtils.hasText(fromAddress);
    }

    @Override
    public void sendTemporaryPassword(String recipient, String temporaryPassword) {
        if (!isConfigured()) {
            logger.error("Resend email sender is not configured");
            throw new IllegalStateException(RESEND_ERROR_MESSAGE);
        }

        initializeWebClient();

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", fromAddress);
        payload.put("to", List.of(recipient));
        payload.put("subject", "Recuperacion de contrasena temporal");
        payload.put("text", buildTextBody(temporaryPassword));
        payload.put("html", buildHtmlBody(temporaryPassword));
        if (StringUtils.hasText(replyTo)) {
            payload.put("reply_to", replyTo);
        }

        try {
            webClient.post()
                    .uri("/emails")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("Resend API error {} while sending password recovery email: {}",
                    e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new IllegalStateException(RESEND_ERROR_MESSAGE, e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending password recovery email with Resend", e);
            throw new IllegalStateException(RESEND_ERROR_MESSAGE, e);
        }
    }

    private void initializeWebClient() {
        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(RESEND_BASE_URL)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.USER_AGENT, "app-horario/1.0")
                    .build();
        }
    }

    private String buildTextBody(String temporaryPassword) {
        return """
                Hola,

                Se ha solicitado la recuperacion de tu cuenta en app-horario.

                Tu contrasena temporal es: %s

                Usa esta contrasena para iniciar sesion y cambiala lo antes posible.

                Si no has solicitado este cambio, contacta con administracion.
                """.formatted(temporaryPassword);
    }

    private String buildHtmlBody(String temporaryPassword) {
        return """
                <p>Hola,</p>
                <p>Se ha solicitado la recuperacion de tu cuenta en <strong>app-horario</strong>.</p>
                <p>Tu contrasena temporal es: <strong>%s</strong></p>
                <p>Usa esta contrasena para iniciar sesion y cambiala lo antes posible.</p>
                <p>Si no has solicitado este cambio, contacta con administracion.</p>
                """.formatted(temporaryPassword);
    }
}
