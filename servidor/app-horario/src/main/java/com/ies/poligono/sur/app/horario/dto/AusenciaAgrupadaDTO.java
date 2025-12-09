package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDate;
import java.util.List;

import com.ies.poligono.sur.app.horario.model.Ausencia;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AusenciaAgrupadaDTO {
	private LocalDate fecha;
	// TODO: borrar DTO De tramos
//	private List<AusenciaTramoDTO> tramos;
	private List<Ausencia> lstAusenciaFecha;

}
