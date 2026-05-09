package com.ies.poligono.sur.app.horario.apputils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GuardiaPointsUtilsTest {

	@Test
	void calcularPuntosGuardiaDevuelve0SiNullOVacio() {
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia(null)).isEqualTo(0);
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("")).isEqualTo(0);
	}

	@Test
	void calcularPuntosGuardiaDevuelve4ParaPrimeroYSegundoESO() {
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("1ER ESO")).isEqualTo(4);
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("2DO ESO")).isEqualTo(4);
	}

	@Test
	void calcularPuntosGuardiaDevuelve3ParaTerceroYCuartoESO() {
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("3ER ESO")).isEqualTo(3);
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("4TO ESO")).isEqualTo(3);
	}

	@Test
	void calcularPuntosGuardiaDevuelve2ParaBachYGradoMedio() {
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("1ER BACH")).isEqualTo(2);
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("2DO BACHILLERATO")).isEqualTo(2);
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("Grado Medio Informatica")).isEqualTo(2);
	}

	@Test
	void calcularPuntosGuardiaDevuelve1ParaGradoSuperior() {
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("Grado Superior DAM")).isEqualTo(1);
	}

	@Test
	void calcularPuntosGuardiaDevuelve0PorDefecto() {
		assertThat(GuardiaPointsUtils.calcularPuntosGuardia("Curso raro")).isEqualTo(0);
	}
}

