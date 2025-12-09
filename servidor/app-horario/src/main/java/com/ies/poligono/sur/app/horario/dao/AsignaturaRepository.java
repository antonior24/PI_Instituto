package com.ies.poligono.sur.app.horario.dao;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.Asignatura;
import com.ies.poligono.sur.app.horario.model.Horario;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

	Asignatura findByNombre(String nombre);
	
	
	@Query("""
	        SELECT h FROM Horario h
	        WHERE h.profesor.id = :idProfesor
	        AND h.dia = :dia
	        AND h.franja.horaInicio >= :horaInicio
	        AND h.franja.horaInicio < :horaFin
	    """)
	    List<Horario> findHorariosEntreHoras(
	        Long idProfesor,
	        String dia,
	        LocalTime horaInicio,
	        LocalTime horaFin
	    );

}