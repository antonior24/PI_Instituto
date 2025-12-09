package com.ies.poligono.sur.app.horario.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.Asignatura;
import com.ies.poligono.sur.app.horario.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

	Curso findByNombre(String nombre);

}