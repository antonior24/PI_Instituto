package com.ies.poligono.sur.app.horario.service;

import java.util.List;

import com.ies.poligono.sur.app.horario.model.Profesor;

public interface ProfesorService {

	Profesor findByNombre(String nombre);

	List<Profesor> buscarPorNombreParcial(String nombre);

	Profesor findById(Long id);

	List<Profesor> obtenerTodos();

	Profesor insertar(Profesor profesor);

	Profesor findByEmailUsuario(String email);

	Long obtenerIdProfesorPorUsername(String email);

	Profesor findByIdUsuario(Long idUsuario);

}
