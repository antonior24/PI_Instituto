package com.ies.poligono.sur.app.horario.apputils;

import java.time.LocalDate;

public class AusenciasUtils {

	/**
	 * Devuelve el día abreviado (L, M, X, J, V) a partir de una fecha.
	 * 
	 * @param fecha Fecha completa
	 * @return Día abreviado
	 */

	// --------------------------------------------------------------------------
	// MÉTODO: obtenerDiaAbreviado
	// Descripción: Devuelve una abreviatura del día (L, M, X, J, V) según la fecha
	// --------------------------------------------------------------------------
	public static String obtenerDiaSemanaByFecha(LocalDate fecha) {
		return switch (fecha.getDayOfWeek()) {
		case MONDAY -> "L";
		case TUESDAY -> "M";
		case WEDNESDAY -> "X";
		case THURSDAY -> "J";
		case FRIDAY -> "V";
		default -> throw new IllegalArgumentException("El día debe estar entre lunes y viernes");
		};
	}
}
