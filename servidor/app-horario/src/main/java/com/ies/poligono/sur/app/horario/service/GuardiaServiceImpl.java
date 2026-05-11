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
import com.ies.poligono.sur.app.horario.service.AusenciaService;

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

		// Obtener el horario a cubrir (la clase en la que hay ausencia)
		Horario horarioCobertura = horarioRepository.findById(dto.getIdHorarioCobertura())
				.orElseThrow(() -> new IllegalArgumentException("Horario a cubrir no encontrado."));

		// Verificar que exista una ausencia para ese horario en esa fecha
		if (!ausenciaService.obtenerIdHorariosConAusencias(dto.getFecha())
				.contains(dto.getIdHorarioCobertura())) {
			throw new IllegalArgumentException(
					"No se puede cubrir una guardia de una clase sin ausencia en esa fecha.");
		}

		// Verificar que el profesor tenga horarios de guardia en su horario
		List<Horario> horariosGuardiaProfesor = horarioRepository.findAll().stream()
				.filter(h -> h.getProfesor() != null && h.getProfesor().getIdProfesor().equals(idProfesor))
				.filter(h -> h.getAsignatura() != null && h.getAsignatura().getNombre().contains("Guardia"))
				.toList();

		if (horariosGuardiaProfesor.isEmpty()) {
			throw new IllegalArgumentException("No tienes horas de guardia asignadas en tu horario.");
		}

		// Verificar que el profesor tenga una guardia en la misma franja que la ausencia
		boolean coincideFranja = horariosGuardiaProfesor.stream()
				.anyMatch(h -> h.getFranja() != null && horarioCobertura.getFranja() != null
						&& h.getFranja().getIdFranja().equals(horarioCobertura.getFranja().getIdFranja()));

		if (!coincideFranja) {
			throw new IllegalArgumentException(
					"No tienes una hora de guardia que coincida con la franja de la ausencia.");
		}

		// Verificar que no haya ya una guardia para ese horario en esa fecha
		if (guardiaRepository.existsByHorarioCobertura_IdAndFecha(dto.getIdHorarioCobertura(), dto.getFecha())) {
			throw new IllegalArgumentException("Ya hay una guardia registrada para ese horario en esa fecha.");
		}

		// Comprobar que el profesor no tenga otra guardia que solape en la misma fecha
		// Obtener las guardias del profesor en esa fecha
		List<Guardia> guardiasProfesorMismoDia = guardiaRepository
				.findByProfesor_IdProfesorAndFecha(idProfesor, dto.getFecha());
		if (guardiasProfesorMismoDia != null && !guardiasProfesorMismoDia.isEmpty()) {
			// obtener la franja de la cobertura que se va a registrar
			if (horarioCobertura.getFranja() != null) {
				java.time.LocalTime nuevoInicio = horarioCobertura.getFranja().getHoraInicio();
				java.time.LocalTime nuevoFin = horarioCobertura.getFranja().getHoraFin();
				for (Guardia g : guardiasProfesorMismoDia) {
					if (g.getHorarioCobertura() != null && g.getHorarioCobertura().getFranja() != null) {
						java.time.LocalTime inicioExistente = g.getHorarioCobertura().getFranja().getHoraInicio();
						java.time.LocalTime finExistente = g.getHorarioCobertura().getFranja().getHoraFin();
						// Comprobar solapamiento: (inicioA < finB) && (inicioB < finA)
						if (nuevoInicio.isBefore(finExistente) && inicioExistente.isBefore(nuevoFin)) {
							throw new IllegalArgumentException(
								"No puedes registrar dos guardias que se solapen en la misma franja ese día.");
						}
					}
				}
			}
		}

		// Calcular puntos según el curso del horario a cubrir
		Integer puntos = GuardiaPointsUtils.calcularPuntosGuardia(horarioCobertura.getCurso().getNombre());

		// Si no se puede calcular puntos automáticamente, asignar 1 punto por defecto
		if (puntos == 0) {
			puntos = 1;
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
	// MÉTODO: desregistrarGuardia
	// Descripción: Desregistra una guardia validando que sea del profesor o admin
	// --------------------------------------------------------------------------
	@Override
	public void desregistrarGuardia(Long idGuardia, Long idProfesor) {
		Guardia guardia = guardiaRepository.findById(idGuardia)
				.orElseThrow(() -> new IllegalArgumentException("Guardia no encontrada."));

		// Si se proporciona idProfesor, validar que sea el propietario de la guardia
		if (idProfesor != null && !guardia.getProfesor().getIdProfesor().equals(idProfesor)) {
			throw new IllegalArgumentException(
					"No tienes permiso para desregistrar una guardia que no registraste.");
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
		dto.setIdHorarioCobertura(guardia.getHorarioCobertura().getId());
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
