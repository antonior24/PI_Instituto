package com.ies.poligono.sur.app.horario.apputils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class AusenciasUtilsTest {

	@Test
	void obtenerDiaSemanaByFechaDevuelveAbreviaturasLaborables() {
		assertThat(AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 4, 27))).isEqualTo("L"); // Monday
		assertThat(AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 4, 28))).isEqualTo("M"); // Tuesday
		assertThat(AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 4, 29))).isEqualTo("X"); // Wednesday
		assertThat(AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 4, 30))).isEqualTo("J"); // Thursday
		assertThat(AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 5, 1))).isEqualTo("V"); // Friday
	}

	@Test
	void obtenerDiaSemanaByFechaLanzaEnFinDeSemana() {
		assertThatThrownBy(() -> AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 5, 2))) // Saturday
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> AusenciasUtils.obtenerDiaSemanaByFecha(LocalDate.of(2026, 5, 3))) // Sunday
				.isInstanceOf(IllegalArgumentException.class);
	}
}

