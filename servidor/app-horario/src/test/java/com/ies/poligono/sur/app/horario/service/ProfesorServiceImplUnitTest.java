package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.ProfesorRepository;
import com.ies.poligono.sur.app.horario.model.Profesor;

@ExtendWith(MockitoExtension.class)
class ProfesorServiceImplUnitTest {

	@Mock
	private ProfesorRepository profesorRepository;

	@InjectMocks
	private ProfesorServiceImpl profesorService;

	@Test
	void findByEmailUsuarioLanzaSiNoExiste() {
		when(profesorRepository.findByUsuarioEmail("no@iespoligonosur.org")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> profesorService.findByEmailUsuario("no@iespoligonosur.org"))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Profesor no encontrado");
	}

	@Test
	void obtenerIdProfesorPorUsernameDevuelveNullSiNoExiste() {
		when(profesorRepository.findByUsuario_Email("no@iespoligonosur.org")).thenReturn(Optional.empty());

		assertThat(profesorService.obtenerIdProfesorPorUsername("no@iespoligonosur.org")).isNull();
	}

	@Test
	void obtenerIdProfesorPorUsernameDevuelveIdSiExiste() {
		Profesor profesor = new Profesor();
		profesor.setIdProfesor(7L);

		when(profesorRepository.findByUsuario_Email("profe@iespoligonosur.org")).thenReturn(Optional.of(profesor));

		assertThat(profesorService.obtenerIdProfesorPorUsername("profe@iespoligonosur.org")).isEqualTo(7L);
	}
}

