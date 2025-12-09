package com.ies.poligono.sur.app.horario.service;

import com.ies.poligono.sur.app.horario.model.Aula;

public interface AulaService {

	Aula findByCodigo(String nombre);

	Aula insertar(Aula aula);

}
