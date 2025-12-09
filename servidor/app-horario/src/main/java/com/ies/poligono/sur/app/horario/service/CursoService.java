package com.ies.poligono.sur.app.horario.service;

import com.ies.poligono.sur.app.horario.model.Curso;

public interface CursoService {

	public Curso findByNombre(String nombre);

	public Curso insertar(Curso curso);

}
