package com.ies.poligono.sur.app.horario.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.apputils.AusenciasUtils;
import com.ies.poligono.sur.app.horario.dao.AusenciaRepository;
import com.ies.poligono.sur.app.horario.dao.FranjaRepository;
import com.ies.poligono.sur.app.horario.dao.HorarioRepository;
import com.ies.poligono.sur.app.horario.dto.AusenciaAgrupadaDTO;
import com.ies.poligono.sur.app.horario.dto.AusenciaTramoDTO;
import com.ies.poligono.sur.app.horario.dto.PostAusenciasInputDTO;
import com.ies.poligono.sur.app.horario.model.Ausencia;
import com.ies.poligono.sur.app.horario.model.Horario;

@Service
public class AusenciaServiceImpl implements AusenciaService {

	@Autowired
	private HorarioRepository horarioRepository;

	@Autowired
	private AusenciaRepository ausenciaRepository;

	@Autowired
	private FranjaRepository franjaRepository;

	// --------------------------------------------------------------------------
	// MÉTODO: crearAusencia
	// Descripción: Crea nuevas ausencias para un profesor en un rango horario
	// determinado. Comprueba si ya existen ausencias para esos horarios y evita
	// duplicados.
	// --------------------------------------------------------------------------
	@Override
	public void crearAusencia(PostAusenciasInputDTO dto, Long idProfesor) {
		if (dto.getFecha().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede registrar una ausencia en el pasado.");
		}

		LocalTime horaInicio = dto.getHoraInicio() != null ? dto.getHoraInicio() : LocalTime.of(8, 0);
		LocalTime horaFin = dto.getHoraFin() != null ? dto.getHoraFin() : LocalTime.of(14, 0);

		if (horaInicio.isAfter(horaFin)) {
			throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la de fin.");
		}

		String diaAbrev = AusenciasUtils.obtenerDiaSemanaByFecha(dto.getFecha());

		// Buscar todos los horarios que tiene el profesor ese día y en ese rango
		// horario
		List<Horario> horarios = horarioRepository.findHorariosEntreHoras(idProfesor, diaAbrev, horaInicio, horaFin);

		if (horarios.isEmpty()) {
			throw new IllegalArgumentException("No tienes clases asignadas en ese tramo horario.");
		}

		// Obtener ausencias ya registradas de ese profesor ese mismo día
		List<Ausencia> ausenciasDelProfesorEseDia = ausenciaRepository
				.findByFechaAndHorario_Profesor_IdProfesor(dto.getFecha(), idProfesor);

		int creadas = 0;

		for (Horario h : horarios) {
			// Verifica si ya existe una ausencia para ese horario
			boolean yaExiste = ausenciasDelProfesorEseDia.stream()
					.anyMatch(a -> a.getHorario().getId().equals(h.getId()));

			// Si no existe, se crea
			if (!yaExiste) {
				Ausencia a = new Ausencia();
				a.setHorario(h);
				a.setDescripcion(dto.getMotivo());
				a.setFecha(dto.getFecha());
				ausenciaRepository.save(a);
				creadas++;
			}
		}

		if (creadas == 0) {
			throw new IllegalArgumentException("Esa ausencia ya existe.");
		}

	}

	@Override
	public void crearAusenciaV2(PostAusenciasInputDTO dto, Long idProfesor) {

		if (dto.getFecha().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede registrar una ausencia en el pasado.");
		}

		if (dto.getHoraInicio() != null && dto.getHoraFin() != null && dto.getHoraInicio().isAfter(dto.getHoraFin())) {
			throw new IllegalArgumentException("La hora de inicio no puede ser posterior a la de fin.");
		}

		String diaAbrev = AusenciasUtils.obtenerDiaSemanaByFecha(dto.getFecha());
		LocalTime horaInicio = dto.getHoraInicio() != null ? dto.getHoraInicio() : LocalTime.MIN;
		LocalTime horaFin = dto.getHoraFin() != null ? dto.getHoraFin() : LocalTime.of(23, 59, 59);
		// Buscar todos los horarios que tiene el profesor ese día y en ese rango
		// horario
		List<Horario> horarios = horarioRepository.findHorariosEntreHoras(idProfesor, diaAbrev, horaInicio, horaFin);

		if (horarios.isEmpty()) {
			throw new IllegalArgumentException("No tienes clases asignadas en ese tramo horario.");
		}

		// Obtener ausencias ya registradas de ese profesor ese mismo día
		List<Ausencia> lstAusenciasProfFecha = ausenciaRepository
				.findByFechaAndHorario_Profesor_IdProfesor(dto.getFecha(), idProfesor);

		List<Long> lstIdHorarioConAusenciaExistente = lstAusenciasProfFecha.stream().map(a -> a.getHorario().getId())
				.toList();

		List<Horario> lstHorarioCrearAusencia = horarios.stream()
				.filter(h -> !lstIdHorarioConAusenciaExistente.contains(h.getId())).toList();

		for (Horario horario : lstHorarioCrearAusencia) {
			Ausencia a = new Ausencia();
			a.setHorario(horario);
			a.setDescripcion(dto.getMotivo());
			a.setFecha(dto.getFecha());
			ausenciaRepository.save(a);
		}

	}

	// --------------------------------------------------------------------------
	// MÉTODO: eliminarAusenciaPorId
	// Descripción: Elimina una ausencia concreta por su ID
	// --------------------------------------------------------------------------
	@Override
	public void eliminarAusenciaPorId(Long id) {
		if (!ausenciaRepository.existsById(id)) {
			throw new IllegalArgumentException("No se encontró ninguna ausencia con el ID: " + id);
		}

		ausenciaRepository.deleteById(id);
	}

	// --------------------------------------------------------------------------
	// MÉTODO: obtenerAusenciasAgrupadas
	// Descripción: Devuelve todas las ausencias del profesor agrupadas por fecha
	// y organizadas por tramos consecutivos
	// --------------------------------------------------------------------------
	@Override
	public List<AusenciaAgrupadaDTO> obtenerAusenciasAgrupadas(Long idProfesor) {

		// Consulta al repositorio
		List<Ausencia> lstAusencias = ausenciaRepository
				.findByHorarioProfesorIdProfesorOrderByHorarioDiaAscHorarioFranjaIdFranjaAsc(idProfesor);

		// Agrupar por fecha
		Map<LocalDate, List<Ausencia>> mapAusenciasPorFecha = lstAusencias.stream()
				.collect(Collectors.groupingBy(Ausencia::getFecha));

		List<AusenciaAgrupadaDTO> lstAusenciasAgrupadas = new ArrayList<>();

		for (Entry<LocalDate, List<Ausencia>> entry : mapAusenciasPorFecha.entrySet()) {
			LocalDate fecha = entry.getKey();
			List<Ausencia> lstAusenciaFecha = entry.getValue();
			// Ordenar por franja
			lstAusenciaFecha.sort(Comparator.comparingInt(a -> a.getHorario().getFranja().getIdFranja().intValue()));
//			lstAusenciasAgrupadas.add(new AusenciaAgrupadaDTO(fecha, agruparEnTramosConsecutivos(lstAusenciaFecha)));
		}

		return lstAusenciasAgrupadas;
	}

	@Override
	public List<AusenciaAgrupadaDTO> obtenerAusenciasAgrupadasV2(Long idProfesor) {

		// Consulta al repositorio
		List<Ausencia> lstAusencias = ausenciaRepository
				.findByHorarioProfesorIdProfesorOrderByHorarioDiaAscHorarioFranjaIdFranjaAsc(idProfesor);

		// Agrupar por fecha
		Map<LocalDate, List<Ausencia>> mapAusenciasPorFecha = lstAusencias.stream()
				.collect(Collectors.groupingBy(Ausencia::getFecha));

		List<AusenciaAgrupadaDTO> ausenciasAgrupadas = mapAusenciasPorFecha.entrySet().stream()
				.map(entry -> new AusenciaAgrupadaDTO(entry.getKey(), entry.getValue())).toList();

		return ausenciasAgrupadas;
	}

	@Override
	public List<AusenciaAgrupadaDTO> obtenerAusenciasAgrupadasTodas() {
		List<Ausencia> lstAusencias = ausenciaRepository.findAll();

		Map<LocalDate, List<Ausencia>> mapAusenciasPorFecha = lstAusencias.stream()
				.collect(Collectors.groupingBy(Ausencia::getFecha));

		return mapAusenciasPorFecha.entrySet().stream()
				.map(entry -> {
					List<Ausencia> lstAusenciaFecha = entry.getValue();
					lstAusenciaFecha.sort(
							Comparator
									.comparing((Ausencia a) -> a.getHorario().getProfesor().getNombre(),
											Comparator.nullsLast(String::compareToIgnoreCase))
									.thenComparing(a -> a.getHorario().getFranja().getIdFranja())
					);
					return new AusenciaAgrupadaDTO(entry.getKey(), lstAusenciaFecha);
				})
				.toList();
	}

	// --------------------------------------------------------------------------
	// MÉTODO: agruparEnTramosConsecutivos
	// Descripción: Agrupa ausencias consecutivas con el mismo motivo en un solo
	// tramo
	// --------------------------------------------------------------------------
	private List<AusenciaTramoDTO> agruparEnTramosConsecutivos(List<Ausencia> ausencias) {
		List<AusenciaTramoDTO> resultado = new ArrayList<>();
		if (ausencias.isEmpty()) {
			return resultado;
		}

		Ausencia ausenciaActual = ausencias.get(0);
		int franjaInicio = ausenciaActual.getHorario().getFranja().getIdFranja().intValue();
		int franjaFin = franjaInicio;

		List<String> asignaturas = new ArrayList<>();
		List<String> aulas = new ArrayList<>();
		List<String> cursos = new ArrayList<>();
		String motivo = ausenciaActual.getDescripcion();

		// Añade datos del primer horario
		asignaturas.add(ausenciaActual.getHorario().getAsignatura().getNombre());
		String aula = ausenciaActual.getHorario().getAula() != null ? ausenciaActual.getHorario().getAula().getCodigo()
				: "—";
		String curso = ausenciaActual.getHorario().getCurso() != null
				? ausenciaActual.getHorario().getCurso().getNombre()
				: "—";

		aulas.add(aula);
		cursos.add(curso);

		for (int i = 1; i < ausencias.size(); i++) {
			Ausencia siguiente = ausencias.get(i);
			int franjaSiguiente = siguiente.getHorario().getFranja().getIdFranja().intValue();

			// Si es consecutivo y el motivo es igual, se agrupa
			if (franjaSiguiente == franjaFin + 1 && siguiente.getDescripcion().equals(motivo)) {
				franjaFin = franjaSiguiente;
				asignaturas.add(siguiente.getHorario().getAsignatura().getNombre());

				String codigoAula = siguiente.getHorario().getAula() != null
						? siguiente.getHorario().getAula().getCodigo()
						: "—";
				String nombreCurso = siguiente.getHorario().getCurso() != null
						? siguiente.getHorario().getCurso().getNombre()
						: "—";

				aulas.add(codigoAula);
				cursos.add(nombreCurso);
			} else {
				// Si no es consecutivo, se guarda el tramo actual y se inicia uno nuevo
				resultado.add(crearTramo(franjaInicio, franjaFin, asignaturas, aulas, cursos, motivo,
						ausenciaActual.isJustificada()));
				franjaInicio = franjaSiguiente;
				franjaFin = franjaSiguiente;
				asignaturas = new ArrayList<>(List.of(siguiente.getHorario().getAsignatura().getNombre()));
				aulas = new ArrayList<>(List.of(siguiente.getHorario().getAula().getCodigo()));
				cursos = new ArrayList<>(List.of(siguiente.getHorario().getCurso().getNombre()));
				motivo = siguiente.getDescripcion();
			}
		}

		// Último tramo
		resultado.add(crearTramo(franjaInicio, franjaFin, asignaturas, aulas, cursos, motivo,
				ausenciaActual.isJustificada()));
		return resultado;
	}

	// --------------------------------------------------------------------------
	// MÉTODO: crearTramo
	// Descripción: Crea un DTO con todos los datos del tramo (inicio, fin, etc.)
	// --------------------------------------------------------------------------
	private AusenciaTramoDTO crearTramo(int franjaInicio, int franjaFin, List<String> asignaturas, List<String> aulas,
			List<String> cursos, String motivo, boolean justificada) {
		LocalTime horaInicio = franjaRepository.findById((long) franjaInicio).get().getHoraInicio();
		LocalTime horaFin = franjaRepository.findById((long) franjaFin).get().getHoraFin();

		return new AusenciaTramoDTO(horaInicio, horaFin, asignaturas, aulas, cursos, motivo, justificada);
	}

	// --------------------------------------------------------------------------
	// MÉTODO: eliminarAusenciasPorFechaYProfesor
	// Descripción: Borra todas las ausencias de un profesor en una fecha concreta
	// --------------------------------------------------------------------------
	@Override
	public void eliminarAusenciasPorFechaYProfesor(LocalDate fecha, Long idProfesor) {
		List<Ausencia> ausencias = ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(fecha, idProfesor);
		ausenciaRepository.deleteAll(ausencias);
	}

	// --------------------------------------------------------------------------
	// MÉTODO: justificarAusenciasDia
	// Descripción: Marca todas las ausencias de un profesor en una fecha como justificadas
	// --------------------------------------------------------------------------
	@Override
	public void justificarAusenciasDia(LocalDate fecha, Long idProfesor) {
		List<Ausencia> ausencias = ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(fecha, idProfesor);
		for (Ausencia ausencia : ausencias) {
			ausencia.setJustificada(true);
		}
		ausenciaRepository.saveAll(ausencias);
	}

}