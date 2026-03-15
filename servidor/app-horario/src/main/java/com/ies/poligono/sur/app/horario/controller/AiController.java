package com.ies.poligono.sur.app.horario.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.dto.HorarioAiRequestDTO;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.AiService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

@RestController
@RequestMapping("/api/horarios")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private ProfesorService profesorService;

    @PostMapping("/ia")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','PROFESOR')")
    public ResponseEntity<String> consultarIA(@RequestBody HorarioAiRequestDTO dto, Principal principal) {
        Long idProfesor = dto.getIdProfesor();
        if (idProfesor == null) {
            if (principal == null) {
                return ResponseEntity.badRequest().body("Debe indicar idProfesor o estar autenticado");
            }
            Profesor prof = profesorService.findByEmailUsuario(principal.getName());
            if (prof == null) {
                return ResponseEntity.status(404).body("Profesor no encontrado para el usuario autenticado");
            }
            idProfesor = prof.getIdProfesor();
        }
        String resp = aiService.consultarIA(idProfesor, dto.getPregunta());
        return ResponseEntity.ok(resp);
    }
}
