package com.ies.poligono.sur.app.horario.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.dto.AusenciaAgrupadaDTO;
import com.ies.poligono.sur.app.horario.dto.PostAusenciasInputDTO;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.AusenciaService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ausencias")
@RequiredArgsConstructor
public class AusenciaController {

	@Autowired
	private AusenciaService ausenciaService;

	@Autowired
	private ProfesorService profesorService;

	@PostMapping
	@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROFESOR')")
	public ResponseEntity<?> crearAusencia(@RequestBody PostAusenciasInputDTO dto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> roles = auth.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toSet());
		Long idProfesor = null;
		if (roles.contains("ROLE_ADMINISTRADOR") && dto.getIdProfesor() != null) {
			// lógica para administrador
			idProfesor = dto.getIdProfesor();
		} else {
			// lógica para profesor o administrador que no informa profesor
			String email = auth.getName();
			Profesor profesor = profesorService.findByEmailUsuario(email);
			idProfesor = profesor.getIdProfesor();
		}

		ausenciaService.crearAusenciaV2(dto, idProfesor);

		return ResponseEntity.ok().build();
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('PROFESOR', 'ADMINISTRADOR')")
	public ResponseEntity<List<AusenciaAgrupadaDTO>> obtenerAusencias(
			@RequestParam(required = false) Long idusuario,
			Principal principal) {
		
		Profesor profesor = null;
		
		// Si se proporciona idusuario, usar ese para filtrar (solo admin puede hacerlo)
		if (idusuario != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Set<String> roles = auth.getAuthorities().stream()
					.map(r -> r.getAuthority()).collect(Collectors.toSet());
			
			// Solo administrador puede consultar ausencias de otro usuario
			if (roles.contains("ROLE_ADMINISTRADOR")) {
				profesor = profesorService.findByIdUsuario(idusuario);
			} else {
				// Profesor intenta consultar a otro: usar su propio ID
				profesor = profesorService.findByEmailUsuario(principal.getName());
			}
		} else {
			// Si no se proporciona idusuario, usar el usuario autenticado
			profesor = profesorService.findByEmailUsuario(principal.getName());
		}

		System.out.println("EMAIL desde token: " + principal.getName());
		System.out.println("Profesor encontrado: " + profesor.getNombre() + " - ID: " + profesor.getIdProfesor());

		List<AusenciaAgrupadaDTO> ausencias = ausenciaService
				.obtenerAusenciasAgrupadasV2(profesor.getIdProfesor());

		return ResponseEntity.ok(ausencias);
	}

	@GetMapping("/todas")
	@PreAuthorize("hasRole('ADMINISTRADOR')")
	public ResponseEntity<List<AusenciaAgrupadaDTO>> obtenerTodasLasAusencias() {
		List<AusenciaAgrupadaDTO> ausencias = ausenciaService.obtenerAusenciasAgrupadasTodas();
		return ResponseEntity.ok(ausencias);
	}

	@DeleteMapping
	@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROFESOR')")
	public ResponseEntity<Void> eliminarAusencia(
			@RequestBody java.util.Map<String, Object> payload,
			Principal principal) {

		// Si se proporciona un id directo, eliminar esa ausencia
		if (payload.containsKey("id")) {
			Long id = Long.parseLong(payload.get("id").toString());
			ausenciaService.eliminarAusenciaPorId(id);
		}
		// Si se proporciona fecha (y opcionalmente idProfesor), eliminar por fecha
		else if (payload.containsKey("fecha")) {
			String fechaStr = payload.get("fecha").toString();
			LocalDate fecha = LocalDate.parse(fechaStr);
			
			Long idProfesor = null;
			// Si es admin y proporciona idProfesor, usar ese
			if (payload.containsKey("idProfesor")) {
				idProfesor = Long.parseLong(payload.get("idProfesor").toString());
			} else {
				// Si no, usar el del usuario autenticado
				idProfesor = profesorService.obtenerIdProfesorPorUsername(principal.getName());
			}
			
			ausenciaService.eliminarAusenciasPorFechaYProfesor(fecha, idProfesor);
		}

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/justificar-dia")
	@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('PROFESOR')")
	public ResponseEntity<Void> justificarAusenciasDia(
			@RequestBody java.util.Map<String, Object> payload,
			Principal principal) {

		String fechaStr = payload.get("fecha").toString();
		LocalDate fecha = LocalDate.parse(fechaStr);
		
		Long idProfesor = null;
		// Si es admin y proporciona idProfesor, usar ese
		if (payload.containsKey("idProfesor")) {
			idProfesor = Long.parseLong(payload.get("idProfesor").toString());
		} else {
			// Si no, usar el del usuario autenticado
			idProfesor = profesorService.obtenerIdProfesorPorUsername(principal.getName());
		}
		
		ausenciaService.justificarAusenciasDia(fecha, idProfesor);

		return ResponseEntity.noContent().build();
	}

}