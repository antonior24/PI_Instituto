package com.ies.poligono.sur.app.horario.mapper;

import org.springframework.stereotype.Component;

import com.ies.poligono.sur.app.horario.dto.PostAusenciasInputDTO;
import com.ies.poligono.sur.app.horario.model.Ausencia;

@Component("ausenciaMapper")
public class AusenciaMapper {

	public Ausencia dtoToEntity(PostAusenciasInputDTO dto) {
		Ausencia entity = new Ausencia();
		entity.setDescripcion(dto.getMotivo());
		entity.setFecha(dto.getFecha());
		return entity;
	}

}