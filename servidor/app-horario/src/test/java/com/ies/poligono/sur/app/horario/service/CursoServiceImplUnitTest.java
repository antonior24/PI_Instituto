package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.CursoRepository;
import com.ies.poligono.sur.app.horario.model.Curso;

@ExtendWith(MockitoExtension.class)
class CursoServiceImplUnitTest {

	@Mock
	private CursoRepository cursoRepository;

	@InjectMocks
	private CursoServiceImpl cursoService;

	@Test
	void findByNombreDelegatesToRepository() {
		Curso curso = new Curso(1L, "1ER ESO");
		when(cursoRepository.findByNombre("1ER ESO")).thenReturn(curso);

		Curso result = cursoService.findByNombre("1ER ESO");

		assertThat(result).isSameAs(curso);
		verify(cursoRepository).findByNombre("1ER ESO");
	}

	@Test
	void insertarDelegatesToRepository() {
		Curso curso = new Curso(null, "2DO ESO");
		when(cursoRepository.save(curso)).thenReturn(curso);

		Curso result = cursoService.insertar(curso);

		assertThat(result).isSameAs(curso);
		verify(cursoRepository).save(curso);
	}
}

