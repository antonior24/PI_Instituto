package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.AusenciaRepository;
import com.ies.poligono.sur.app.horario.dao.FranjaRepository;
import com.ies.poligono.sur.app.horario.dao.HorarioRepository;
import com.ies.poligono.sur.app.horario.dto.PostAusenciasInputDTO;
import com.ies.poligono.sur.app.horario.dto.ValidarAusenciaRequestDTO;
import com.ies.poligono.sur.app.horario.dto.ValidarAusenciaResponseDTO;
import com.ies.poligono.sur.app.horario.model.Ausencia;
import com.ies.poligono.sur.app.horario.model.Horario;

@ExtendWith(MockitoExtension.class)
class AusenciaServiceImplUnitTest {

	@Mock
	private HorarioRepository horarioRepository;

	@Mock
	private AusenciaRepository ausenciaRepository;

	@Mock
	private FranjaRepository franjaRepository;

	@InjectMocks
	private AusenciaServiceImpl ausenciaService;

	@Test
	void crearAusenciaFallaSiFechaEnPasado() {
		PostAusenciasInputDTO dto = new PostAusenciasInputDTO();
		dto.setFecha(LocalDate.now().minusDays(1));

		assertThatThrownBy(() -> ausenciaService.crearAusencia(dto, 1L))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void crearAusenciaFallaSiHoraInicioPosterior() {
		PostAusenciasInputDTO dto = new PostAusenciasInputDTO();
		dto.setFecha(LocalDate.now().plusDays(1));
		dto.setHoraInicio(LocalTime.of(12, 0));
		dto.setHoraFin(LocalTime.of(10, 0));

		assertThatThrownBy(() -> ausenciaService.crearAusencia(dto, 1L))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void crearAusenciaFallaSiNoHayClases() {
		PostAusenciasInputDTO dto = new PostAusenciasInputDTO();
		dto.setFecha(LocalDate.now().plusDays(1));

		when(horarioRepository.findHorariosEntreHoras(any(), any(), any(), any()))
				.thenReturn(List.of());

		assertThatThrownBy(() -> ausenciaService.crearAusencia(dto, 1L))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void crearAusenciaFallaSiTodasYaExisten() {
		PostAusenciasInputDTO dto = new PostAusenciasInputDTO();
		dto.setFecha(LocalDate.now().plusDays(1));
		dto.setMotivo("Motivo");

		Horario h1 = new Horario();
		h1.setId(10L);
		Horario h2 = new Horario();
		h2.setId(11L);

		when(horarioRepository.findHorariosEntreHoras(any(), any(), any(), any()))
				.thenReturn(List.of(h1, h2));

		Ausencia a1 = new Ausencia();
		a1.setHorario(h1);
		Ausencia a2 = new Ausencia();
		a2.setHorario(h2);

		when(ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(dto.getFecha(), 1L))
				.thenReturn(List.of(a1, a2));

		assertThatThrownBy(() -> ausenciaService.crearAusencia(dto, 1L))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void crearAusenciaGuardaNuevasAusencias() {
		PostAusenciasInputDTO dto = new PostAusenciasInputDTO();
		dto.setFecha(LocalDate.now().plusDays(1));
		dto.setMotivo("Motivo");
		dto.setHoraInicio(LocalTime.of(8, 0));
		dto.setHoraFin(LocalTime.of(14, 0));

		Horario h1 = new Horario();
		h1.setId(10L);
		Horario h2 = new Horario();
		h2.setId(11L);

		when(horarioRepository.findHorariosEntreHoras(any(), any(), any(), any()))
				.thenReturn(List.of(h1, h2));

		when(ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(dto.getFecha(), 1L))
				.thenReturn(List.of());

		ausenciaService.crearAusencia(dto, 1L);

		verify(ausenciaRepository, times(2)).save(any(Ausencia.class));
	}

	@Test
	void validarAusenciaDevuelveErrorSiDtoONFechaNula() {
		ValidarAusenciaResponseDTO r1 = ausenciaService.validarAusencia(null, 1L);
		assertThat(r1.isValido()).isFalse();

		ValidarAusenciaRequestDTO dto = new ValidarAusenciaRequestDTO();
		ValidarAusenciaResponseDTO r2 = ausenciaService.validarAusencia(dto, 1L);
		assertThat(r2.isValido()).isFalse();
	}

	@Test
	void validarAusenciaDevuelveErrorSiHoraInicioPosterior() {
		ValidarAusenciaRequestDTO dto = new ValidarAusenciaRequestDTO();
		dto.setFecha(LocalDate.now().plusDays(1));
		dto.setHoraInicio(LocalTime.of(12, 0));
		dto.setHoraFin(LocalTime.of(10, 0));

		ValidarAusenciaResponseDTO r = ausenciaService.validarAusencia(dto, 1L);
		assertThat(r.isValido()).isFalse();
	}

	@Test
	void validarAusenciaDevuelveErrorSiDiaNoLaborable() {
		ValidarAusenciaRequestDTO dto = new ValidarAusenciaRequestDTO();
		dto.setFecha(LocalDate.of(2026, 5, 2)); // Saturday

		ValidarAusenciaResponseDTO r = ausenciaService.validarAusencia(dto, 1L);
		assertThat(r.isValido()).isFalse();
	}

	@Test
	void validarAusenciaDevuelveErrorSiNoHayClases() {
		ValidarAusenciaRequestDTO dto = new ValidarAusenciaRequestDTO();
		dto.setFecha(LocalDate.now().plusDays(1));

		when(horarioRepository.findHorariosEntreHoras(any(), any(), any(), any())).thenReturn(List.of());

		ValidarAusenciaResponseDTO r = ausenciaService.validarAusencia(dto, 1L);
		assertThat(r.isValido()).isFalse();
	}

	@Test
	void validarAusenciaDevuelveErrorSiHayConflictos() {
		ValidarAusenciaRequestDTO dto = new ValidarAusenciaRequestDTO();
		dto.setFecha(LocalDate.of(2026, 4, 27)); // Monday

		Horario h1 = new Horario();
		h1.setId(10L);
		Horario h2 = new Horario();
		h2.setId(11L);

		when(horarioRepository.findHorariosEntreHoras(any(), any(), any(), any())).thenReturn(List.of(h1, h2));

		Ausencia a1 = new Ausencia();
		a1.setHorario(h1);
		when(ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(dto.getFecha(), 1L)).thenReturn(List.of(a1));

		ValidarAusenciaResponseDTO r = ausenciaService.validarAusencia(dto, 1L);
		assertThat(r.isValido()).isFalse();
		assertThat(r.getAusenciasExistentes()).isEqualTo(1);
	}

	@Test
	void validarAusenciaDevuelveOkSiNoHayConflictos() {
		ValidarAusenciaRequestDTO dto = new ValidarAusenciaRequestDTO();
		dto.setFecha(LocalDate.of(2026, 4, 27)); // Monday

		Horario h1 = new Horario();
		h1.setId(10L);
		when(horarioRepository.findHorariosEntreHoras(any(), any(), any(), any())).thenReturn(List.of(h1));
		when(ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(dto.getFecha(), 1L)).thenReturn(List.of());

		ValidarAusenciaResponseDTO r = ausenciaService.validarAusencia(dto, 1L);
		assertThat(r.isValido()).isTrue();
		assertThat(r.getAusenciasExistentes()).isEqualTo(0);
	}

	@Test
	void eliminarAusenciasPorFechaYProfesorBorraTodas() {
		LocalDate fecha = LocalDate.now().plusDays(1);
		Ausencia a1 = new Ausencia();
		Ausencia a2 = new Ausencia();

		when(ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(fecha, 1L)).thenReturn(List.of(a1, a2));

		ausenciaService.eliminarAusenciasPorFechaYProfesor(fecha, 1L);

		verify(ausenciaRepository).deleteAll(List.of(a1, a2));
	}

	@Test
	void justificarAusenciasDiaMarcaJustificadasYGuarda() {
		LocalDate fecha = LocalDate.now().plusDays(1);
		Ausencia a1 = new Ausencia();
		Ausencia a2 = new Ausencia();

		when(ausenciaRepository.findByFechaAndHorario_Profesor_IdProfesor(fecha, 1L)).thenReturn(List.of(a1, a2));

		ausenciaService.justificarAusenciasDia(fecha, 1L);

		ArgumentCaptor<List<Ausencia>> captor = ArgumentCaptor.forClass(List.class);
		verify(ausenciaRepository).saveAll(captor.capture());
		assertThat(captor.getValue()).allMatch(Ausencia::isJustificada);
	}

	@Test
	void obtenerIdHorariosConAusenciasDevuelveIdsDistintos() {
		LocalDate fecha = LocalDate.now().plusDays(1);

		Horario h1 = new Horario();
		h1.setId(10L);
		Horario h2 = new Horario();
		h2.setId(10L);

		Ausencia a1 = new Ausencia();
		a1.setHorario(h1);
		Ausencia a2 = new Ausencia();
		a2.setHorario(h2);

		when(ausenciaRepository.findByFecha(fecha)).thenReturn(List.of(a1, a2));

		assertThat(ausenciaService.obtenerIdHorariosConAusencias(fecha)).containsExactly(10L);
	}
}

