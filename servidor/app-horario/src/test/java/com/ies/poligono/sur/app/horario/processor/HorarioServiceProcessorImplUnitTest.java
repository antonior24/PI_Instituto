package com.ies.poligono.sur.app.horario.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dto.PostImportacionInputDTO;
import com.ies.poligono.sur.app.horario.model.Asignatura;
import com.ies.poligono.sur.app.horario.model.Aula;
import com.ies.poligono.sur.app.horario.model.Curso;
import com.ies.poligono.sur.app.horario.model.Franja;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.service.AsignaturaService;
import com.ies.poligono.sur.app.horario.service.AulaService;
import com.ies.poligono.sur.app.horario.service.CursoService;
import com.ies.poligono.sur.app.horario.service.FranjaService;
import com.ies.poligono.sur.app.horario.service.HorarioService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;
import com.ies.poligono.sur.app.horario.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class HorarioServiceProcessorImplUnitTest {

	@Mock
	private HorarioService horarioService;

	@Mock
	private AsignaturaService asignaturaService;

	@Mock
	private CursoService cursoService;

	@Mock
	private AulaService aulaService;

	@Mock
	private ProfesorService profesorService;

	@Mock
	private UsuarioService usuarioService;

	@Mock
	private FranjaService franjaService;

	@InjectMocks
	private HorarioServiceProcessorImpl processor;

	@Test
	void importarHorarioInsertaDosLineasValidasYBorraAnterior() {
		String txt = ""
				+ "Matematicas\t1ER ESO\tA1\tPerez, Juan\tL\t1\n"
				+ "Lengua\t2DO ESO\tB2\tPerez, Juan\tM\t1\n";
		PostImportacionInputDTO input = new PostImportacionInputDTO();
		input.setFile(base64(txt));

		Franja franja = new Franja(1L, LocalTime.of(8, 15), LocalTime.of(9, 15));
		when(franjaService.findById(1L)).thenReturn(Optional.of(franja));

		when(asignaturaService.findByNombre(any())).thenReturn(null);
		when(asignaturaService.insertar(any(Asignatura.class))).thenAnswer(inv -> inv.getArgument(0));

		when(cursoService.findByNombre(any())).thenReturn(null);
		when(cursoService.insertar(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));

		when(aulaService.findByCodigo(any())).thenReturn(null);
		when(aulaService.insertar(any(Aula.class))).thenAnswer(inv -> inv.getArgument(0));

		when(profesorService.findByNombre(any())).thenReturn(null);
		when(usuarioService.generarEmailDesdeNombre(any())).thenReturn("juan.perez@iespoligonosur.org");
		when(usuarioService.crearUsuario(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
		when(profesorService.insertar(any(Profesor.class))).thenAnswer(inv -> inv.getArgument(0));

		processor.importarHorario(input);

		verify(horarioService).borrarTodosLosHorarios();
		verify(horarioService, times(2)).crearHorario(any());
	}

	@Test
	void importarHorarioIgnoraLineaConFranjaInvalida() {
		String txt = ""
				+ "Matematicas\t1ER ESO\tA1\tPerez, Juan\tL\tX\n"
				+ "Lengua\t2DO ESO\tB2\tPerez, Juan\tM\t1\n";
		PostImportacionInputDTO input = new PostImportacionInputDTO();
		input.setFile(base64(txt));

		Franja franja = new Franja(1L, LocalTime.of(8, 15), LocalTime.of(9, 15));
		when(franjaService.findById(1L)).thenReturn(Optional.of(franja));

		when(asignaturaService.findByNombre(any())).thenReturn(new Asignatura(1L, "x"));
		when(cursoService.findByNombre(any())).thenReturn(new Curso(1L, "x"));
		when(aulaService.findByCodigo(any())).thenReturn(new Aula(1L, "x"));
		when(profesorService.findByNombre(any())).thenReturn(new Profesor(1L, null, "x"));

		processor.importarHorario(input);

		verify(horarioService).borrarTodosLosHorarios();
		verify(horarioService, times(1)).crearHorario(any());
	}

	@Test
	void importarHorarioNoCreaProfesorSiYaExiste() {
		String txt = "Matematicas\t1ER ESO\tA1\tProfesor Existente\tL\t1\n";
		PostImportacionInputDTO input = new PostImportacionInputDTO();
		input.setFile(base64(txt));

		when(franjaService.findById(1L)).thenReturn(Optional.of(new Franja(1L, LocalTime.of(8, 15), LocalTime.of(9, 15))));
		when(asignaturaService.findByNombre(any())).thenReturn(new Asignatura(1L, "Matematicas"));
		when(cursoService.findByNombre(any())).thenReturn(new Curso(1L, "1ER ESO"));
		when(aulaService.findByCodigo(any())).thenReturn(new Aula(1L, "A1"));

		Profesor profesor = new Profesor();
		profesor.setIdProfesor(10L);
		profesor.setNombre("Profesor Existente");
		when(profesorService.findByNombre(eq("Profesor Existente"))).thenReturn(profesor);

		processor.importarHorario(input);

		verify(usuarioService, never()).crearUsuario(any());
		verify(profesorService, never()).insertar(any());
		verify(horarioService, times(1)).crearHorario(any());
	}

	private static String base64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
	}
}

