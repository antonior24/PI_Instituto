package com.ies.poligono.sur.app.horario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dao.AsignaturaRepository;
import com.ies.poligono.sur.app.horario.model.Asignatura;

@Service
public class AsignaturaServiceImpl implements AsignaturaService {

	@Autowired
	AsignaturaRepository asignaturaRepository;

	@Override
	public Asignatura findByNombre(String nombre) {
		return asignaturaRepository.findByNombre(nombre);
	}

	@Override
	public Asignatura insertar(Asignatura asignatura) {
		return asignaturaRepository.save(asignatura);
	}
}
