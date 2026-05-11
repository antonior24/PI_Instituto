package com.ies.poligono.sur.app.horario.controller;

import com.ies.poligono.sur.app.horario.dao.ActividadRepository;
import com.ies.poligono.sur.app.horario.model.Actividad;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping({"/api/tracking", "/tracking"})
@CrossOrigin(origins = "*") // Permite que el front de Vue conecte sin problemas
public class TrackingController {

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private ProfesorService profesorService;

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
            @RequestParam(required = false) Long profesorId,
            @RequestParam(required = false) Long idProfesor,
            @RequestParam(required = false, defaultValue = "false") boolean global,
            Principal principal) {
        try {
            System.out.println("🔍 TrackingController: Recibido - usuario=" + usuario + ", profesorId=" + profesorId + ", idProfesor=" + idProfesor + ", global=" + global);

            Long profesorIdReal = profesorId != null ? profesorId : idProfesor;
            if (profesorIdReal != null) {
                System.out.println("👨‍🏫 Buscando profesor con ID: " + profesorIdReal);
                Profesor profesor = profesorService.findById(profesorIdReal);
                if (profesor == null) {
                    System.out.println("❌ Profesor no encontrado para ID: " + profesorIdReal);
                    return ResponseEntity.badRequest().body("Profesor no encontrado.");
                }
                if (profesor.getUsuario() == null) {
                    System.out.println("❌ Profesor encontrado pero sin usuario asociado: " + profesor.getNombre());
                    return ResponseEntity.badRequest().body("Profesor sin usuario asociado.");
                }
                usuario = profesor.getUsuario().getEmail();
                System.out.println("✅ Filtrando estadísticas para usuario: " + usuario + " (profesor: " + profesor.getNombre() + ")");
            } else {
                System.out.println("📊 No se especificó profesorId, usando usuario autenticado o global");
            }

            if (!global && usuario == null) {
                if (principal != null) {
                    usuario = principal.getName();
                    System.out.println("👤 Usando usuario autenticado: " + usuario);
                } else {
                    System.out.println("❌ No hay usuario autenticado ni profesorId especificado");
                    return ResponseEntity.badRequest().body("Debe iniciar sesión o usar global=true para estadísticas globales.");
                }
            }

            Map<String, Object> stats = new HashMap<>();
            if (global) {
                System.out.println("🌍 Obteniendo estadísticas globales");
                stats.put("porTipo", actividadRepository.countEventosPorTipo());
                stats.put("porPagina", actividadRepository.countVisitasPorPagina());
            } else {
                System.out.println("👤 Obteniendo estadísticas para usuario: " + usuario);
                stats.put("porTipo", actividadRepository.countEventosPorTipoByUsuario(usuario));
                stats.put("porPagina", actividadRepository.countVisitasPorPaginaByUsuario(usuario));
            }
            System.out.println("✅ Estadísticas obtenidas correctamente");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.out.println("❌ Error al obtener stats: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al obtener stats: " + e.getMessage());
        }
    }
}