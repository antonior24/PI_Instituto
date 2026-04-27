package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ies.poligono.sur.app.horario.dao.FranjaRepository;
import com.ies.poligono.sur.app.horario.model.Franja;

@ExtendWith(MockitoExtension.class)
class FranjaServiceImplUnitTest {

	@Mock
	private FranjaRepository franjaRepository;

	@InjectMocks
	private FranjaServiceImpl franjaService;

	@Test
	void findAllDelegatesToRepository() {
		List<Franja> franjas = List.of(new Franja(1L, LocalTime.of(8, 0), LocalTime.of(9, 0)));
		when(franjaRepository.findAll()).thenReturn(franjas);

		assertThat(franjaService.findAll()).isSameAs(franjas);
		verify(franjaRepository).findAll();
	}

	@Test
	void findByIdDelegatesToRepository() {
		Franja franja = new Franja(1L, LocalTime.of(8, 0), LocalTime.of(9, 0));
		when(franjaRepository.findById(1L)).thenReturn(Optional.of(franja));

		assertThat(franjaService.findById(1L)).containsSame(franja);
		verify(franjaRepository).findById(1L);
	}

	@Test
	void saveDelegatesToRepository() {
		Franja franja = new Franja(null, LocalTime.of(10, 0), LocalTime.of(11, 0));
		when(franjaRepository.save(franja)).thenReturn(franja);

		assertThat(franjaService.save(franja)).isSameAs(franja);
		verify(franjaRepository).save(franja);
	}

	@Test
	void deleteByIdDelegatesToRepository() {
		franjaService.deleteById(7L);
		verify(franjaRepository).deleteById(7L);
	}
}

