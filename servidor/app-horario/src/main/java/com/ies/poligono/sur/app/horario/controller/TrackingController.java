package com.ies.poligono.sur.app.horario.controller;

import com.ies.poligono.sur.app.horario.dao.ActividadRepository;
import com.ies.poligono.sur.app.horario.model.Actividad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*") // Permite que el front de Vue conecte sin problemas
public class TrackingController {

    @Autowired
    private ActividadRepository actividadRepository;

    @PostMapping("/batch")
    public ResponseEntity<String> guardarActividad(@RequestBody List<Actividad> actividades, Principal principal) {
        try {
            if (principal != null) {
                String usuarioLogueado = principal.getName();
                for (Actividad actividad : actividades) {
                    actividad.setUsuario(usuarioLogueado);
                }
            }
            actividadRepository.saveAll(actividades);
            return ResponseEntity.ok("Registros guardados");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> obtenerEstadisticas(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false, defaultValue = "false") boolean global,
            Principal principal) {
        try {
            if (!global && usuario == null) {
                if (principal != null) {
                    usuario = principal.getName();
                } else {
                    return ResponseEntity.badRequest().body("Debe iniciar sesión o usar global=true para estadísticas globales.");
                }
            }

            Map<String, Object> stats = new HashMap<>();
            if (global) {
                stats.put("porTipo", actividadRepository.countEventosPorTipo());
                stats.put("porPagina", actividadRepository.countVisitasPorPagina());
            } else {
                stats.put("porTipo", actividadRepository.countEventosPorTipoByUsuario(usuario));
                stats.put("porPagina", actividadRepository.countVisitasPorPaginaByUsuario(usuario));
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener stats: " + e.getMessage());
        }
    }
}