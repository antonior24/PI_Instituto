package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.AulaRepository;
import com.ies.poligono.sur.app.horario.model.Aula;

@ExtendWith(MockitoExtension.class)
class AulaServiceImplUnitTest {

	@Mock
	private AulaRepository aulaRepository;

	@InjectMocks
	private AulaServiceImpl aulaService;

	@Test
	void findByCodigoDelegatesToRepository() {
		Aula aula = new Aula(1L, "A1");
		when(aulaRepository.findByCodigo("A1")).thenReturn(aula);

		Aula result = aulaService.findByCodigo("A1");

		assertThat(result).isSameAs(aula);
		verify(aulaRepository).findByCodigo("A1");
	}

	@Test
	void insertarDelegatesToRepository() {
		Aula aula = new Aula(null, "B2");
		when(aulaRepository.save(aula)).thenReturn(aula);

		Aula result = aulaService.insertar(aula);

		assertThat(result).isSameAs(aula);
		verify(aulaRepository).save(aula);
	}
}

