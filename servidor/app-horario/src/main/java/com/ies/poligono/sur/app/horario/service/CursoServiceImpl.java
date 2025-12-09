package com.ies.poligono.sur.app.horario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dao.CursoRepository;
import com.ies.poligono.sur.app.horario.model.Curso;

@Service
public class CursoServiceImpl implements CursoService {

	@Autowired
	CursoRepository cursoRepository;

	@Override
	public Curso findByNombre(String nombre) {
		return cursoRepository.findByNombre(nombre);
	}

	@Override
	public Curso insertar(Curso curso) {
		return cursoRepository.save(curso);
	}
}
