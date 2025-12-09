package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AusenciaTramoDTO {
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private List<String> asignaturas;
	private List<String> aulas;
	private List<String> cursos;
	private String motivo;
	private boolean justificada;

}
