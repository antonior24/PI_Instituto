package com.ies.poligono.sur.app.horario.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ies.poligono.sur.app.horario.model.Horario;

@Service
public class AiServiceImpl implements AiService {

    private final HorarioService horarioService;
    private WebClient webClient;

    @Value("${google.gemini.api.key:}")
    private String apiKey;

    public AiServiceImpl(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    private void initializeWebClient() {
        if (this.webClient == null) {
            System.out.println("[AiService-Gemini] Inicializando. API Key presente: " + (apiKey != null && !apiKey.isEmpty()));
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.err.println("[AiService-Gemini] ERROR: google.gemini.api.key no configurada. Establece GOOGLE_GEMINI_API_KEY");
                throw new IllegalStateException("google.gemini.api.key no está configurada. Establece la variable GOOGLE_GEMINI_API_KEY");
            }
            this.webClient = WebClient.builder()
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            System.out.println("[AiService-Gemini] WebClient inicializado correctamente");
        }
    }

    @Override
    public String consultarIA(Long idProfesor, String pregunta) {
        initializeWebClient();
        
        // Recupera horarios del profesor
        List<Horario> horarios = horarioService.obtenerPorProfesor(idProfesor);
        StringBuilder contexto = new StringBuilder();
        contexto.append("Eres un asistente que ayuda a responder preguntas sobre horarios escolares.\n\n");
        contexto.append("Este es el horario del profesor:\n");
        for (Horario h : horarios) {
            contexto.append(String.format("- %s, franja %d: %s (%s) aula %s curso %s\n", 
                    h.getDia(),
                    h.getFranja() != null ? h.getFranja().getIdFranja() : "?",
                    h.getAsignatura() != null ? h.getAsignatura().getNombre() : "Sin asignar",
                    h.getProfesor() != null ? h.getProfesor().getNombre() : "?",
                    h.getAula() != null ? h.getAula().getCodigo() : "-",
                    h.getCurso() != null ? h.getCurso().getNombre() : "-"));
        }
        contexto.append("\nPregunta del usuario: ").append(pregunta);

        // Construir request para Gemini API
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = List.of(
            Map.of("parts", List.of(
                Map.of("text", contexto.toString())
            ))
        );
        requestBody.put("contents", contents);

        try {
            String url = "/models/gemini-2.5-flash:generateContent?key=" + apiKey;
            System.out.println("[AiService-Gemini] Enviando solicitud a: " + url);
            
            Map<?, ?> response = webClient.post()
                    .uri(url)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("[AiService-Gemini] Respuesta recibida: " + response);

            if (response != null) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Object firstCandidate = candidates.get(0);
                    if (firstCandidate instanceof Map<?, ?> m) {
                        Object contentObj = m.get("content");
                        if (contentObj instanceof Map<?, ?> content) {
                            List<?> parts = (List<?>) content.get("parts");
                            if (parts != null && !parts.isEmpty()) {
                                Object firstPart = parts.get(0);
                                if (firstPart instanceof Map<?, ?> part) {
                                    Object text = part.get("text");
                                    if (text instanceof String s) {
                                        return s;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return "No se pudo obtener respuesta de Gemini";
        } catch (WebClientResponseException e) {
            System.err.println("[AiService-Gemini] Error HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 403 || e.getStatusCode().value() == 401) {
                return "Error de autenticación: Verifica que tu clave de Gemini sea válida. Detalles: " + e.getResponseBodyAsString();
            } else if (e.getStatusCode().value() == 429) {
                return "Error 429: Has excedido el límite de solicitudes. Intenta más tarde.";
            }
            return "Error al contactar con Gemini: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            System.err.println("[AiService-Gemini] Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return "Error inesperado: " + e.getMessage();
        }
    }
}

