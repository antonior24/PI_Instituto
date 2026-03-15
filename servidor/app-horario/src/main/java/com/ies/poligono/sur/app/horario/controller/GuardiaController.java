package com.ies.poligono.sur.app.horario.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.dto.GuardiaResponseDTO;
import com.ies.poligono.sur.app.horario.dto.RegistrarGuardiaDTO;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.GuardiaService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guardias")
@RequiredArgsConstructor
public class GuardiaController {

	@Autowired
	private GuardiaService guardiaService;

	@Autowired
	private ProfesorService profesorService;

	// --------------------------------------------------------------------------
	// POST: Registrar una guardia
	// --------------------------------------------------------------------------
	@PostMapping
	@PreAuthorize("hasRole('PROFESOR') or hasRole('ADMINISTRADOR')")
	public ResponseEntity<GuardiaResponseDTO> registrarGuardia(
			@RequestBody RegistrarGuardiaDTO dto,
			Principal principal) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> roles = auth.getAuthorities().stream()
				.map(r -> r.getAuthority())
				.collect(Collectors.toSet());

		Long idProfesor = null;

		// Si es admin y especifica otro profesor, usar ese ID
		if (roles.contains("ROLE_ADMINISTRADOR") && dto.getIdProfesor() != null) {
			idProfesor = dto.getIdProfesor();
		} else {
			// Si no, usar el profesor autenticado
			Profesor profesor = profesorService.findByEmailUsuario(principal.getName());
			idProfesor = profesor.getIdProfesor();
		}

		try {
			GuardiaResponseDTO response = guardiaService.registrarGuardia(dto, idProfesor);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// --------------------------------------------------------------------------
	// GET: Obtener guardias de un profesor
	// --------------------------------------------------------------------------
	@GetMapping("/profesor")
	@PreAuthorize("hasRole('PROFESOR') or hasRole('ADMINISTRADOR')")
	public ResponseEntity<List<GuardiaResponseDTO>> obtenerGuardias(
			@RequestParam(required = false) Long idProfesor,
			Principal principal) {

		Long idProfessorToFetch = idProfesor;

		// Si no se especifica profesor, usar el del token
		if (idProfessorToFetch == null) {
			Profesor profesor = profesorService.findByEmailUsuario(principal.getName());
			idProfessorToFetch = profesor.getIdProfesor();
		}

		List<GuardiaResponseDTO> guardias = guardiaService.obtenerGuardiasPorProfesor(idProfessorToFetch);
		return ResponseEntity.ok(guardias);
	}

	// --------------------------------------------------------------------------
	// GET: Obtener guardias por fecha
	// --------------------------------------------------------------------------
	@GetMapping("/fecha")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<List<GuardiaResponseDTO>> obtenerGuardiasPorFecha(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

		List<GuardiaResponseDTO> guardias = guardiaService.obtenerGuardiasPorFecha(fecha);
		return ResponseEntity.ok(guardias);
	}

	// --------------------------------------------------------------------------
	// GET: Obtener puntos totales de un profesor
	// --------------------------------------------------------------------------
	@GetMapping("/puntos")
	@PreAuthorize("hasRole('PROFESOR') or hasRole('ADMINISTRADOR')")
	public ResponseEntity<Integer> obtenerPuntosTotales(
			@RequestParam(required = false) Long idProfesor,
			Principal principal) {

		Long idProfessorToFetch = idProfesor;

		// Si no se especifica profesor, usar el del token
		if (idProfessorToFetch == null) {
			Profesor profesor = profesorService.findByEmailUsuario(principal.getName());
			idProfessorToFetch = profesor.getIdProfesor();
		}

		Integer puntos = guardiaService.obtenerPuntosTotalesPorProfesor(idProfessorToFetch);
		return ResponseEntity.ok(puntos);
	}

	// --------------------------------------------------------------------------
	// DELETE: Eliminar una guardia
	// --------------------------------------------------------------------------
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<Void> eliminarGuardia(@PathVariable Long id) {
		try {
			guardiaService.eliminarGuardia(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
