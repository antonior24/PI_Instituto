package com.ies.poligono.sur.app.horario.dao;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ies.poligono.sur.app.horario.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

	
	@Query("""
	        SELECT h FROM Horario h
	        JOIN h.franja f
	        WHERE h.profesor.id = :idProfesor
	        AND h.dia = :dia
	        AND f.horaInicio >= :horaInicio
	        AND f.horaInicio < :horaFin
	    """)
	    List<Horario> findHorariosEntreHoras(
	        Long idProfesor,
	        String dia,
	        LocalTime horaInicio,
	        LocalTime horaFin
	    );
	
}
