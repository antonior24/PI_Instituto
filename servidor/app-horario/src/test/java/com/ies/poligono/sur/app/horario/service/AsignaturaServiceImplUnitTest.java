package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.AsignaturaRepository;
import com.ies.poligono.sur.app.horario.model.Asignatura;

@ExtendWith(MockitoExtension.class)
class AsignaturaServiceImplUnitTest {

	@Mock
	private AsignaturaRepository asignaturaRepository;

	@InjectMocks
	private AsignaturaServiceImpl asignaturaService;

	@Test
	void findByNombreDelegatesToRepository() {
		Asignatura asignatura = new Asignatura(1L, "Matematicas");
		when(asignaturaRepository.findByNombre("Matematicas")).thenReturn(asignatura);

		Asignatura result = asignaturaService.findByNombre("Matematicas");

		assertThat(result).isSameAs(asignatura);
		verify(asignaturaRepository).findByNombre("Matematicas");
	}

	@Test
	void insertarDelegatesToRepository() {
		Asignatura asignatura = new Asignatura(null, "Lengua");
		when(asignaturaRepository.save(asignatura)).thenReturn(asignatura);

		Asignatura result = asignaturaService.insertar(asignatura);

		assertThat(result).isSameAs(asignatura);
		verify(asignaturaRepository).save(asignatura);
	}
}

