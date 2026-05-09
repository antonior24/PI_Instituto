package com.ies.poligono.sur.app.horario.apputils;

import java.text.Normalizer;

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

		String curso = Normalizer.normalize(nombreCurso, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toUpperCase()
				.replaceAll("[^A-Z0-9]+", " ")
				.trim();

		boolean esEso = curso.contains("ESO");
		boolean esBachillerato = curso.contains("BACH") || curso.contains("BACHILLERATO");
		boolean esGradoBasico = curso.contains("GRADO BASICO");
		boolean esGradoMedio = curso.contains("GRADO MEDIO");
		boolean esGradoSuperior = curso.contains("GRADO SUPERIOR");

		// 1º y 2º ESO: 4 puntos
		if ((curso.startsWith("1") || curso.contains(" 1 ")) && esEso) {
			return 4;
		}
		if ((curso.startsWith("2") || curso.contains(" 2 ")) && esEso) {
			return 4;
		}

		// 3º y 4º ESO y Grado básico: 3 puntos
		if ((curso.startsWith("3") || curso.contains(" 3 ")) && esEso) {
			return 3;
		}
		if ((curso.startsWith("4") || curso.contains(" 4 ")) && esEso) {
			return 3;
		}
		if (esGradoBasico) {
			return 3;
		}

		// 1º y 2º BACH y Grados medios: 2 puntos
		if ((curso.startsWith("1") || curso.contains(" 1 ")) && esBachillerato) {
			return 2;
		}
		if ((curso.startsWith("2") || curso.contains(" 2 ")) && esBachillerato) {
			return 2;
		}
		if (esGradoMedio) {
			return 2;
		}

		// Grado superior: 1 punto
		if (esGradoSuperior) {
			return 1;
		}

		// Por defecto
		return 0;
	}
}
