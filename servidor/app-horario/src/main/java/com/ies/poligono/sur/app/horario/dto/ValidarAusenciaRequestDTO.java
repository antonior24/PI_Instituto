package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class ValidarAusenciaRequestDTO {
	private LocalDate fecha;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private Long idProfesor;
}
