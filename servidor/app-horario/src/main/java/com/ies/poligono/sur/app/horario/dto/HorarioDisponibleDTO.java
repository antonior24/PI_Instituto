package com.ies.poligono.sur.app.horario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponibleDTO {

	private Long id;

	private String curso;

	private String asignatura;

	private String aula;

	private String dia;

	private String horaInicio;

	private String horaFin;

	private Integer puntos;
}
