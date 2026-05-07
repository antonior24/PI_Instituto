package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardiaResponseDTO {

	private Long id;

	private Long idProfesor;

	private String nombreProfesor;

	private Long idHorarioCobertura;

	private Long idCursoCobertura;

	private String nombreCursoCobertura;

	private LocalDate fecha;

	private Integer puntos;

	private String asignatura;

	private String aula;

	private String franja;
}
