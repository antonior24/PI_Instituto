package com.ies.poligono.sur.app.horario.service;

import com.ies.poligono.sur.app.horario.model.Asignatura;

public interface AsignaturaService {

	Asignatura findByNombre(String nombre);
	
	Asignatura insertar(Asignatura asignatura);

}
