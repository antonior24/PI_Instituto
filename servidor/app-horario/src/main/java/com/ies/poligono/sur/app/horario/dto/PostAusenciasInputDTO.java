package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostAusenciasInputDTO {

	@NotNull(message = "La fecha no puede ser nula")
	private LocalDate fecha;

	private LocalTime horaInicio;

	private LocalTime horaFin;

	private String motivo;
	
	private Long idProfesor;
	
}
