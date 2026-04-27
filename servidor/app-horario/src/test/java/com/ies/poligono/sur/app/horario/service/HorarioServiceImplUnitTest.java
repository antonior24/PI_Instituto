package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.HorarioRepository;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;

@ExtendWith(MockitoExtension.class)
class HorarioServiceImplUnitTest {

	@Mock
	private HorarioRepository horarioRepository;

	@InjectMocks
	private HorarioServiceImpl horarioService;

	@Test
	void crearHorarioDelegatesToRepository() {
		Horario horario = new Horario();
		when(horarioRepository.save(horario)).thenReturn(horario);

		assertThat(horarioService.crearHorario(horario)).isSameAs(horario);
		verify(horarioRepository).save(horario);
	}

	@Test
	void borrarTodosLosHorariosDelegatesToRepository() {
		horarioService.borrarTodosLosHorarios();
		verify(horarioRepository).deleteAll();
	}

	@Test
	void obtenerTodosDelegatesToRepository() {
		List<Horario> horarios = List.of(new Horario(), new Horario());
		when(horarioRepository.findAll()).thenReturn(horarios);

		assertThat(horarioService.obtenerTodos()).isSameAs(horarios);
		verify(horarioRepository).findAll();
	}

	@Test
	void obtenerPorProfesorFiltraSoloLosQueCoinciden() {
		Profesor p1 = new Profesor();
		p1.setIdProfesor(1L);
		Profesor p2 = new Profesor();
		p2.setIdProfesor(2L);

		Horario h1 = new Horario();
		h1.setId(10L);
		h1.setProfesor(p1);

		Horario h2 = new Horario();
		h2.setId(11L);
		h2.setProfesor(p2);

		Horario h3 = new Horario();
		h3.setId(12L);
		h3.setProfesor(null);

		when(horarioRepository.findAll()).thenReturn(List.of(h1, h2, h3));

		List<Horario> result = horarioService.obtenerPorProfesor(1L);

		assertThat(result).containsExactly(h1);
	}
}

