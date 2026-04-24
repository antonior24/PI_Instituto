package com.ies.poligono.sur.app.horario.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.apputils.GuardiaPointsUtils;
import com.ies.poligono.sur.app.horario.dto.HorarioDisponibleDTO;
import com.ies.poligono.sur.app.horario.dto.PostImportacionInputDTO;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.processor.HorarioServiceProcessor;
import com.ies.poligono.sur.app.horario.service.HorarioService;
import com.ies.poligono.sur.app.horario.service.HorarioPDFService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

	@Autowired
	HorarioServiceProcessor horarioServiceProcessor;
	
	@Autowired
	HorarioService horarioService;

	@Autowired
	ProfesorService profesorService;

	@Autowired
	HorarioPDFService horarioPDFService;

	// Endpoint para obtener horarios
	@GetMapping
	public ResponseEntity<List<Horario>> obtenerHorarios(@RequestParam(required = false) Long idProfesor) {
		List<Horario> horarios;
		if (idProfesor != null) {
			horarios = horarioService.obtenerPorProfesor(idProfesor);
		} else {
			horarios = horarioService.obtenerTodos();
		}
		return ResponseEntity.ok(horarios);
	}

	// Endpoint para obtener los horarios del profesor autenticado
	@GetMapping("/mis-horarios")
	@PreAuthorize("hasAnyRole('PROFESOR', 'ADMINISTRADOR')")
	public ResponseEntity<List<HorarioDisponibleDTO>> obtenerMisHorarios(Principal principal) {
		try {
			if (principal == null || principal.getName() == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			Profesor profesor = profesorService.findByEmailUsuario(principal.getName());
			if (profesor == null) {
				System.out.println("❌ Profesor no encontrado para email: " + principal.getName());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			List<Horario> horarios = horarioService.obtenerPorProfesor(profesor.getIdProfesor());
			System.out.println("✅ Horarios encontrados para profesor " + profesor.getNombre() + ": " + horarios.size());
			
			// Convertir a DTO con puntos calculados
			List<HorarioDisponibleDTO> horariosDTO = horarios.stream()
				.map(h -> {
					HorarioDisponibleDTO dto = new HorarioDisponibleDTO();
					dto.setId(h.getId());
					dto.setCurso(h.getCurso() != null ? h.getCurso().getNombre() : "—");
					dto.setAsignatura(h.getAsignatura() != null ? h.getAsignatura().getNombre() : "—");
					dto.setAula(h.getAula() != null ? h.getAula().getCodigo() : "—");
					dto.setDia(h.getDia());
					dto.setHoraInicio(h.getFranja() != null ? h.getFranja().getHoraInicio().toString() : "—");
					dto.setHoraFin(h.getFranja() != null ? h.getFranja().getHoraFin().toString() : "—");
					dto.setPuntos(GuardiaPointsUtils.calcularPuntosGuardia(h.getCurso() != null ? h.getCurso().getNombre() : ""));
					return dto;
				})
				.collect(Collectors.toList());
			
			return ResponseEntity.ok(horariosDTO);
		} catch (Exception e) {
			System.err.println("❌ Error al obtener mis horarios: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Endpoint para descargar el horario en PDF
	@GetMapping("/pdf/mis-horarios")
	@PreAuthorize("hasAnyRole('PROFESOR', 'ADMINISTRADOR')")
	public ResponseEntity<byte[]> descargarHorarioPDF(Principal principal) {
		try {
			if (principal == null || principal.getName() == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			Profesor profesor = profesorService.findByEmailUsuario(principal.getName());
			if (profesor == null) {
				System.out.println("❌ Profesor no encontrado para email: " + principal.getName());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			List<Horario> horarios = horarioService.obtenerPorProfesor(profesor.getIdProfesor());
			System.out.println("✅ Generando PDF del horario para profesor " + profesor.getNombre() + ": " + horarios.size() + " clases");
			
			// Generar el PDF
			byte[] pdfBytes = horarioPDFService.generarHorarioPDF(profesor, horarios);
			
			// Retornar el PDF como descarga
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header("Content-Disposition", "attachment; filename=horario-" + profesor.getNombre().replaceAll("\\s+", "_") + ".pdf")
					.body(pdfBytes);
					
		} catch (Exception e) {
			System.err.println("❌ Error al generar el PDF del horario: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Endpoint para subir el archivo
	@PostMapping("/importacion")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> importacion(@RequestBody PostImportacionInputDTO inputDTO) {
		horarioServiceProcessor.importarHorario(inputDTO);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Horarios importados correctamente");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
