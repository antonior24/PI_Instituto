package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ies.poligono.sur.app.horario.dao.GuardiaRepository;
import com.ies.poligono.sur.app.horario.dao.HorarioRepository;
import com.ies.poligono.sur.app.horario.dao.ProfesorRepository;
import com.ies.poligono.sur.app.horario.dto.GuardiaResponseDTO;
import com.ies.poligono.sur.app.horario.dto.RegistrarGuardiaDTO;
import com.ies.poligono.sur.app.horario.model.Asignatura;
import com.ies.poligono.sur.app.horario.model.Aula;
import com.ies.poligono.sur.app.horario.model.Curso;
import com.ies.poligono.sur.app.horario.model.Franja;
import com.ies.poligono.sur.app.horario.model.Guardia;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuardiaServiceImplUnitTest {

  @Mock private GuardiaRepository guardiaRepository;

  @Mock private HorarioRepository horarioRepository;

  @Mock private ProfesorRepository profesorRepository;

  @Mock private AusenciaService ausenciaService;

  @InjectMocks private GuardiaServiceImpl guardiaService;

  @Test
  void registrarGuardiaFallaSiFechaPasada() {
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(1L, LocalDate.now().minusDays(1), null);

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiProfesorNoExiste() {
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(1L, LocalDate.now().plusDays(1), null);
    when(profesorRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiHorarioCoberturaNoExiste() {
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(1L, LocalDate.now().plusDays(1), null);
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(new Profesor()));
    when(horarioRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiNoHayAusenciaEnEseHorario() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(10L, fecha, null);

    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

    Horario cobertura = new Horario();
    cobertura.setId(10L);
    cobertura.setCurso(new Curso(1L, "1ER ESO"));
    cobertura.setFranja(new Franja(1L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findById(10L)).thenReturn(Optional.of(cobertura));

    when(ausenciaService.obtenerIdHorariosConAusencias(fecha)).thenReturn(List.of());

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiProfesorNoTieneHorasDeGuardia() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(10L, fecha, null);

    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

    Horario cobertura = new Horario();
    cobertura.setId(10L);
    cobertura.setCurso(new Curso(1L, "1ER ESO"));
    cobertura.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findById(10L)).thenReturn(Optional.of(cobertura));

    when(ausenciaService.obtenerIdHorariosConAusencias(fecha)).thenReturn(List.of(10L));
    when(horarioRepository.findAll()).thenReturn(List.of()); // no guardia hours

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiNoCoincideFranja() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(10L, fecha, null);

    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

    Horario cobertura = new Horario();
    cobertura.setId(10L);
    cobertura.setCurso(new Curso(1L, "1ER ESO"));
    cobertura.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findById(10L)).thenReturn(Optional.of(cobertura));

    when(ausenciaService.obtenerIdHorariosConAusencias(fecha)).thenReturn(List.of(10L));

    Horario guardia = new Horario();
    guardia.setProfesor(profesor);
    guardia.setAsignatura(new Asignatura(1L, "Guardia"));
    guardia.setFranja(new Franja(99L, LocalTime.of(9, 15), LocalTime.of(10, 15)));
    when(horarioRepository.findAll()).thenReturn(List.of(guardia));

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiYaExisteGuardia() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(10L, fecha, null);

    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

    Horario cobertura = new Horario();
    cobertura.setId(10L);
    cobertura.setCurso(new Curso(1L, "1ER ESO"));
    cobertura.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findById(10L)).thenReturn(Optional.of(cobertura));

    when(ausenciaService.obtenerIdHorariosConAusencias(fecha)).thenReturn(List.of(10L));

    Horario guardia = new Horario();
    guardia.setProfesor(profesor);
    guardia.setAsignatura(new Asignatura(1L, "Guardia"));
    guardia.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findAll()).thenReturn(List.of(guardia));

    when(guardiaRepository.existsByHorarioCobertura_IdAndFecha(10L, fecha)).thenReturn(true);

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaFallaSiNoSePuedenCalcularPuntos() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(10L, fecha, null);

    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

    Horario cobertura = new Horario();
    cobertura.setId(10L);
    cobertura.setCurso(new Curso(1L, "Curso raro"));
    cobertura.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findById(10L)).thenReturn(Optional.of(cobertura));

    when(ausenciaService.obtenerIdHorariosConAusencias(fecha)).thenReturn(List.of(10L));

    Horario guardia = new Horario();
    guardia.setProfesor(profesor);
    guardia.setAsignatura(new Asignatura(1L, "Guardia"));
    guardia.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findAll()).thenReturn(List.of(guardia));

    when(guardiaRepository.existsByHorarioCobertura_IdAndFecha(10L, fecha)).thenReturn(false);

    assertThatThrownBy(() -> guardiaService.registrarGuardia(dto, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void registrarGuardiaCasoOkGuardaYMapeaDTO() {
    LocalDate fecha = LocalDate.now().plusDays(1);
    RegistrarGuardiaDTO dto = new RegistrarGuardiaDTO(10L, fecha, null);

    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    profesor.setNombre("Profe");
    when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

    Franja franja = new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15));
    Curso curso = new Curso(2L, "1ER ESO");
    Asignatura asignatura = new Asignatura(3L, "Matematicas");
    Aula aula = new Aula(4L, "A1");

    Horario cobertura = new Horario();
    cobertura.setId(10L);
    cobertura.setCurso(curso);
    cobertura.setFranja(franja);
    cobertura.setAsignatura(asignatura);
    cobertura.setAula(aula);
    when(horarioRepository.findById(10L)).thenReturn(Optional.of(cobertura));

    when(ausenciaService.obtenerIdHorariosConAusencias(fecha)).thenReturn(List.of(10L));

    Horario guardia = new Horario();
    guardia.setProfesor(profesor);
    guardia.setAsignatura(new Asignatura(1L, "Guardia"));
    guardia.setFranja(new Franja(5L, LocalTime.of(8, 15), LocalTime.of(9, 15)));
    when(horarioRepository.findAll()).thenReturn(List.of(guardia));

    when(guardiaRepository.existsByHorarioCobertura_IdAndFecha(10L, fecha)).thenReturn(false);
    when(guardiaRepository.save(any(Guardia.class))).thenAnswer(inv -> inv.getArgument(0));

    GuardiaResponseDTO response = guardiaService.registrarGuardia(dto, 1L);

    assertThat(response.getIdProfesor()).isEqualTo(1L);
    assertThat(response.getNombreProfesor()).isEqualTo("Profe");
    assertThat(response.getIdCursoCobertura()).isEqualTo(2L);
    assertThat(response.getNombreCursoCobertura()).isEqualTo("1ER ESO");
    assertThat(response.getFecha()).isEqualTo(fecha);
    assertThat(response.getPuntos()).isEqualTo(4);
    assertThat(response.getAsignatura()).isEqualTo("Matematicas");
    assertThat(response.getAula()).isEqualTo("A1");
    assertThat(response.getFranja()).contains("08:15").contains("09:15");

    ArgumentCaptor<Guardia> captor = ArgumentCaptor.forClass(Guardia.class);
    verify(guardiaRepository).save(captor.capture());
    assertThat(captor.getValue().getFechaRegistro()).isNotNull();
    assertThat(captor.getValue().getFechaRegistro()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  void obtenerPuntosTotalesSumaCorrectamente() {
    Guardia g1 = new Guardia();
    g1.setPuntos(2);
    Guardia g2 = new Guardia();
    g2.setPuntos(3);
    when(guardiaRepository.findByProfesor_IdProfesor(1L)).thenReturn(List.of(g1, g2));

    assertThat(guardiaService.obtenerPuntosTotalesPorProfesor(1L)).isEqualTo(5);
  }

  @Test
  void eliminarGuardiaFallaSiNoExiste() {
    when(guardiaRepository.findById(99L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> guardiaService.eliminarGuardia(99L, null, true))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void eliminarGuardiaEliminaSiExisteYEsAdmin() {
    Guardia guardia = new Guardia();
    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    guardia.setProfesor(profesor);
    when(guardiaRepository.findById(99L)).thenReturn(Optional.of(guardia));

    guardiaService.eliminarGuardia(99L, null, true);

    verify(guardiaRepository).deleteById(99L);
  }

  @Test
  void eliminarGuardiaFallaSiProfesorNoEsPropietario() {
    Guardia guardia = new Guardia();
    Profesor profesor = new Profesor();
    profesor.setIdProfesor(1L);
    guardia.setProfesor(profesor);
    when(guardiaRepository.findById(99L)).thenReturn(Optional.of(guardia));

    assertThatThrownBy(() -> guardiaService.eliminarGuardia(99L, 2L, false))
        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
  }
}
