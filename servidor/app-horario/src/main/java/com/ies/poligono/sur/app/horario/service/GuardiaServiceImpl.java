package com.ies.poligono.sur.app.horario.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.apputils.GuardiaPointsUtils;
import com.ies.poligono.sur.app.horario.dao.GuardiaRepository;
import com.ies.poligono.sur.app.horario.dao.HorarioRepository;
import com.ies.poligono.sur.app.horario.dao.ProfesorRepository;
import com.ies.poligono.sur.app.horario.dto.GuardiaResponseDTO;
import com.ies.poligono.sur.app.horario.dto.RegistrarGuardiaDTO;
import com.ies.poligono.sur.app.horario.model.Guardia;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;

@Service
public class GuardiaServiceImpl implements GuardiaService {

	@Autowired
	private GuardiaRepository guardiaRepository;

	@Autowired
	private HorarioRepository horarioRepository;

	@Autowired
	private ProfesorRepository profesorRepository;

	@Autowired
	private AusenciaService ausenciaService;

	// --------------------------------------------------------------------------
	// MÉTODO: registrarGuardia
	// Descripción: Un profesor registra que cubre una clase y se le asignan puntos
	// --------------------------------------------------------------------------
	@Override
	public GuardiaResponseDTO registrarGuardia(RegistrarGuardiaDTO dto, Long idProfesor) {

		if (dto.getFecha().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede registrar una guardia en el pasado.");
		}

		// Obtener el profesor (del token o del DTO si es admin)
		Profesor profesor = profesorRepository.findById(idProfesor)
				.orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado."));

		// Obtener el horario a cubrir
		Horario horarioCobertura = horarioRepository.findById(dto.getIdHorarioCobertura())
				.orElseThrow(() -> new IllegalArgumentException("Horario a cubrir no encontrado."));

		// NUEVA VALIDACIÓN 1: Verificar que el horario a cubrir tenga una ausencia en esa fecha
		if (!ausenciaService.obtenerIdHorariosConAusencias(dto.getFecha())
				.contains(dto.getIdHorarioCobertura())) {
			throw new IllegalArgumentException(
					"No se puede cubrir una guardia de un horario que no tiene ausencia registrada en esa fecha.");
		}

		// NUEVA VALIDACIÓN 2: Verificar que el profesor tiene guardia en su horario
		List<Horario> horariosProfesor = horarioRepository.findAll().stream()
				.filter(h -> h.getProfesor().getIdProfesor().equals(idProfesor))
				.toList();

		if (horariosProfesor.isEmpty()) {
			throw new IllegalArgumentException("El profesor no tiene horarios de guardia asignados.");
		}

		// NUEVA VALIDACIÓN 3: Verificar que el professor tenga una guardia que coincida con la franja
		boolean tieneGuardiaEnFranja = horariosProfesor.stream()
				.anyMatch(h -> h.getFranja().getIdFranja().equals(horarioCobertura.getFranja().getIdFranja())
						&& h.getAsignatura().getNombre().contains("Guardia"));

		if (!tieneGuardiaEnFranja) {
			throw new IllegalArgumentException(
					"El profesor no tiene una guardia asignada en esa franja horaria.");
		}

		// Verificar que no haya ya una guardia para ese horario en esa fecha
		if (guardiaRepository.existsByHorarioCobertura_IdAndFecha(dto.getIdHorarioCobertura(), dto.getFecha())) {
			throw new IllegalArgumentException("Ya hay una guardia registrada para ese horario en esa fecha.");
		}

		// Calcular puntos según el curso del horario a cubrir
		Integer puntos = GuardiaPointsUtils.calcularPuntosGuardia(horarioCobertura.getCurso().getNombre());

		if (puntos == 0) {
			throw new IllegalArgumentException(
					"No se puede calcular puntos para el curso: " + horarioCobertura.getCurso().getNombre());
		}

		// Crear la guardia
		Guardia guardia = new Guardia();
		guardia.setProfesor(profesor);
		guardia.setHorarioCobertura(horarioCobertura);
		guardia.setFecha(dto.getFecha());
		guardia.setPuntos(puntos);
		guardia.setFechaRegistro(LocalDateTime.now());

		guardiaRepository.save(guardia);

		// Construir el DTO response
		return mapToGuardiaResponseDTO(guardia);
	}

	// --------------------------------------------------------------------------
	// MÉTODO: obtenerGuardiasPorProfesor
	// Descripción: Obtiene todas las guardias de un profesor
	// --------------------------------------------------------------------------
	@Override
	public List<GuardiaResponseDTO> obtenerGuardiasPorProfesor(Long idProfesor) {
		List<Guardia> guardias = guardiaRepository.findByProfesor_IdProfesor(idProfesor);
		return guardias.stream().map(this::mapToGuardiaResponseDTO).collect(Collectors.toList());
	}

	// --------------------------------------------------------------------------
	// MÉTODO: obtenerGuardiasPorFecha
	// Descripción: Obtiene todas las guardias registradas en una fecha
	// --------------------------------------------------------------------------
	@Override
	public List<GuardiaResponseDTO> obtenerGuardiasPorFecha(LocalDate fecha) {
		List<Guardia> guardias = guardiaRepository.findByFecha(fecha);
		return guardias.stream().map(this::mapToGuardiaResponseDTO).collect(Collectors.toList());
	}

	// --------------------------------------------------------------------------
	// MÉTODO: obtenerPuntosTotalesPorProfesor
	// Descripción: Suma los puntos totales de un profesor
	// --------------------------------------------------------------------------
	@Override
	public Integer obtenerPuntosTotalesPorProfesor(Long idProfesor) {
		List<Guardia> guardias = guardiaRepository.findByProfesor_IdProfesor(idProfesor);
		return guardias.stream().mapToInt(g -> g.getPuntos()).sum();
	}

	// --------------------------------------------------------------------------
	// MÉTODO: eliminarGuardia
	// Descripción: Elimina una guardia por su ID
	// --------------------------------------------------------------------------
	@Override
	public void eliminarGuardia(Long idGuardia) {
		if (!guardiaRepository.existsById(idGuardia)) {
			throw new IllegalArgumentException("Guardia no encontrada.");
		}
		guardiaRepository.deleteById(idGuardia);
	}

	// --------------------------------------------------------------------------
	// MÉTODO: mapToGuardiaResponseDTO
	// Descripción: Convierte una entidad Guardia a DTO
	// --------------------------------------------------------------------------
	private GuardiaResponseDTO mapToGuardiaResponseDTO(Guardia guardia) {
		GuardiaResponseDTO dto = new GuardiaResponseDTO();
		dto.setId(guardia.getId());
		dto.setIdProfesor(guardia.getProfesor().getIdProfesor());
		dto.setNombreProfesor(guardia.getProfesor().getNombre());
		dto.setIdCursoCobertura(guardia.getHorarioCobertura().getCurso().getIdCurso());
		dto.setNombreCursoCobertura(guardia.getHorarioCobertura().getCurso().getNombre());
		dto.setFecha(guardia.getFecha());
		dto.setPuntos(guardia.getPuntos());

		if (guardia.getHorarioCobertura().getAsignatura() != null) {
			dto.setAsignatura(guardia.getHorarioCobertura().getAsignatura().getNombre());
		}

		if (guardia.getHorarioCobertura().getAula() != null) {
			dto.setAula(guardia.getHorarioCobertura().getAula().getCodigo());
		}

		if (guardia.getHorarioCobertura().getFranja() != null) {
			String franja = guardia.getHorarioCobertura().getFranja().getHoraInicio() + " - "
					+ guardia.getHorarioCobertura().getFranja().getHoraFin();
			dto.setFranja(franja);
		}

		return dto;
	}
}
