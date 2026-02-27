package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidarAusenciaResponseDTO {
	private boolean valido;
	private String mensaje;
	private LocalDate fecha;
	private String dia;
	private int clasesEnTramo;
	private int ausenciasExistentes;
}
