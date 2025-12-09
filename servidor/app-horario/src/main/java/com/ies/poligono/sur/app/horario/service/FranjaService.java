package com.ies.poligono.sur.app.horario.service;

import java.util.List;
import java.util.Optional;

import com.ies.poligono.sur.app.horario.model.Franja;

public interface FranjaService {

	Franja save(Franja franja);

	void deleteById(Long id);

	Optional<Franja> findById(Long id);

	List<Franja> findAll();

	
}
