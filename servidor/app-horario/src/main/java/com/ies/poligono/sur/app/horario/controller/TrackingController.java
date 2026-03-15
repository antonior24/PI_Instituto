package com.ies.poligono.sur.app.horario.controller;

import com.ies.poligono.sur.app.horario.dao.ActividadRepository;
import com.ies.poligono.sur.app.horario.model.Actividad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<String> guardarActividad(@RequestBody List<Actividad> actividades) {
        try {
            actividadRepository.saveAll(actividades);
            return ResponseEntity.ok("Registros guardados");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("porTipo", actividadRepository.countEventosPorTipo());
            stats.put("porPagina", actividadRepository.countVisitasPorPagina());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener stats: " + e.getMessage());
        }
    }
}