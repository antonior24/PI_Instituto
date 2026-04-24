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
import com.ies.poligono.sur.app.horario.dto.HorarioDisponibleDTO;
import com.ies.poligono.sur.app.horario.dto.RegistrarGuardiaDTO;
import com.ies.poligono.sur.app.horario.model.Ausencia;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.AusenciaService;
import com.ies.poligono.sur.app.horario.service.GuardiaService;
import com.ies.poligono.sur.app.horario.service.HorarioService;
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

	@Autowired
	private AusenciaService ausenciaService;

	@Autowired
	private HorarioService horarioService;

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
	// GET: Obtener horarios disponibles para guardia (clases con ausencias)
	// --------------------------------------------------------------------------
	@GetMapping("/horarios-disponibles")
	@PreAuthorize("hasRole('PROFESOR') or hasRole('ADMINISTRADOR')")
	public ResponseEntity<List<HorarioDisponibleDTO>> obtenerHorariosDisponiblesParaGuardia(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			@RequestParam(required = false) Long idProfesor,
			Principal principal) {

		Long idProfesorAPedir = idProfesor;

		// Solo el profesor autenticado (salvo admin que puede pasar id)
		if (idProfesorAPedir == null) {
			Profesor profesor = profesorService.findByEmailUsuario(principal.getName());
			idProfesorAPedir = profesor.getIdProfesor();
		}

		// Obtener todas las ausencias de esa fecha (de todos los profesores)
		List<Ausencia> ausencias = ausenciaService.obtenerAusenciasDeUnaFecha(fecha);

		if (ausencias.isEmpty()) {
			return ResponseEntity.ok(java.util.Collections.emptyList());
		}

		// Obtener las franjas de guardia del profesor (solo sus horarios marcados como guardia)
		List<Horario> horariosGuardia = horarioService.obtenerPorProfesor(idProfesorAPedir).stream()
				.filter(h -> h.getAsignatura() != null && h.getAsignatura().getNombre().contains("Guardia"))
				.toList();

		if (horariosGuardia.isEmpty()) {
			return ResponseEntity.ok(java.util.Collections.emptyList());
		}

		java.util.Set<Long> franjasGuardia = horariosGuardia.stream()
				.map(Horario::getFranja)
				.filter(f -> f != null)
				.map(f -> f.getIdFranja())
				.collect(Collectors.toSet());

		java.util.Set<Long> idsVistos = new java.util.HashSet<>();
		List<HorarioDisponibleDTO> disponibles = new java.util.ArrayList<>();

		for (Ausencia ausencia : ausencias) {
			Horario horarioAusente = ausencia.getHorario();
			if (horarioAusente == null || horarioAusente.getFranja() == null) continue;
			Long idFranja = horarioAusente.getFranja().getIdFranja();
			if (!franjasGuardia.contains(idFranja)) continue;
			if (horarioAusente.getId() == null || idsVistos.contains(horarioAusente.getId())) continue;
			idsVistos.add(horarioAusente.getId());

			HorarioDisponibleDTO dto = new HorarioDisponibleDTO();
			dto.setId(horarioAusente.getId());
			dto.setCurso(horarioAusente.getCurso() != null ? horarioAusente.getCurso().getNombre() : "—");
			dto.setAsignatura(horarioAusente.getAsignatura() != null ? horarioAusente.getAsignatura().getNombre() : "—");
			dto.setAula(horarioAusente.getAula() != null ? horarioAusente.getAula().getCodigo() : "—");
			dto.setDia(horarioAusente.getDia());
			dto.setHoraInicio(horarioAusente.getFranja().getHoraInicio().toString());
			dto.setHoraFin(horarioAusente.getFranja().getHoraFin().toString());
			dto.setPuntos(0);
			disponibles.add(dto);
		}

		return ResponseEntity.ok(disponibles);
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
