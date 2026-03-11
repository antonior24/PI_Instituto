package com.ies.poligono.sur.app.horario.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.Guardia;

@Repository
public interface GuardiaRepository extends JpaRepository<Guardia, Long> {

	// Obtener todas las guardias de un profesor
	List<Guardia> findByProfesor_IdProfesor(Long idProfesor);

	// Obtener guardias de un profesor en una fecha específica
	List<Guardia> findByProfesor_IdProfesorAndFecha(Long idProfesor, LocalDate fecha);

	// Obtener guardias por fecha
	List<Guardia> findByFecha(LocalDate fecha);

	// Comprobar si existe guardia para un horario específico en una fecha
	boolean existsByHorarioCobertura_IdAndFecha(Long idHorario, LocalDate fecha);

	// Obtener guardias por rango de fechas
	List<Guardia> findByProfesor_IdProfesorAndFechaBetween(Long idProfesor, LocalDate fechaInicio, LocalDate fechaFin);

	// Contar guardias por profesor
	Long countByProfesor_IdProfesor(Long idProfesor);

	// Sumar puntos totales por profesor
	Integer findTotalPuntosByProfesor_IdProfesor(Long idProfesor);
}
