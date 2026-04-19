package com.ies.poligono.sur.app.horario.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.Ausencia;
import com.ies.poligono.sur.app.horario.model.Horario;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, Long> {

	List<Ausencia> findByHorarioProfesorIdProfesorOrderByHorarioDiaAscHorarioFranjaIdFranjaAsc(Long idProfesor);

	List<Ausencia> findByFechaAndHorario_Profesor_IdProfesor(LocalDate fecha, Long idProfesor);

	boolean existsByFechaAndHorario(LocalDate fecha, Horario horario);

	// Todas las ausencias para una fecha (de todos los profesores)
	List<Ausencia> findByFecha(LocalDate fecha);

	// Verifica si hay una ausencia en un horario específico en una fecha
	boolean existsByFechaAndHorario_Id(LocalDate fecha, Long idHorario);

}
