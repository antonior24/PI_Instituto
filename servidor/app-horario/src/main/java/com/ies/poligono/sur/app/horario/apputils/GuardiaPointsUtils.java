package com.ies.poligono.sur.app.horario.apputils;

public class GuardiaPointsUtils {

	/**
	 * Calcula los puntos de guardia según el nivel del curso
	 * 
	 * 1º y 2º ESO: 4 puntos
	 * 3º y 4º ESO y Grado básico: 3 puntos
	 * 1º y 2º BACH y Grados medios: 2 puntos
	 * Grado superiores: 1 punto
	 */
	public static Integer calcularPuntosGuardia(String nombreCurso) {
		if (nombreCurso == null || nombreCurso.isEmpty()) {
			return 0;
		}

		String curso = nombreCurso.toUpperCase().trim();

		// 1º y 2º ESO: 4 puntos
		if (curso.contains("1º") && curso.contains("ESO")) {
			return 4;
		}
		if (curso.contains("1ER") && curso.contains("ESO")) {
			return 4;
		}
		if (curso.contains("2º") && curso.contains("ESO")) {
			return 4;
		}
		if (curso.contains("2DO") && curso.contains("ESO")) {
			return 4;
		}

		// 3º y 4º ESO y Grado básico: 3 puntos
		if (curso.contains("3º") && curso.contains("ESO")) {
			return 3;
		}
		if (curso.contains("3ER") && curso.contains("ESO")) {
			return 3;
		}
		if (curso.contains("4º") && curso.contains("ESO")) {
			return 3;
		}
		if (curso.contains("4TO") && curso.contains("ESO")) {
			return 3;
		}
		if (curso.toUpperCase().contains("GRADO BÁSICO")) {
			return 3;
		}

		// 1º y 2º BACH y Grados medios: 2 puntos
		if (curso.contains("1º") && (curso.contains("BACH") || curso.contains("BACHILLERATO"))) {
			return 2;
		}
		if (curso.contains("1ER") && (curso.contains("BACH") || curso.contains("BACHILLERATO"))) {
			return 2;
		}
		if (curso.contains("2º") && (curso.contains("BACH") || curso.contains("BACHILLERATO"))) {
			return 2;
		}
		if (curso.contains("2DO") && (curso.contains("BACH") || curso.contains("BACHILLERATO"))) {
			return 2;
		}
		if (curso.contains("GRADO MEDIO")) {
			return 2;
		}

		// Grado superior: 1 punto
		if (curso.contains("GRADO SUPERIOR")) {
			return 1;
		}

		// Por defecto
		return 0;
	}
}
