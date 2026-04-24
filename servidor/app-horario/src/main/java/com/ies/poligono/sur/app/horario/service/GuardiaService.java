package com.ies.poligono.sur.app.horario.service;

import java.time.LocalDate;
import java.util.List;

import com.ies.poligono.sur.app.horario.dto.GuardiaResponseDTO;
import com.ies.poligono.sur.app.horario.dto.RegistrarGuardiaDTO;

public interface GuardiaService {

	GuardiaResponseDTO registrarGuardia(RegistrarGuardiaDTO dto, Long idProfesor);

	List<GuardiaResponseDTO> obtenerGuardiasPorProfesor(Long idProfesor);

	List<GuardiaResponseDTO> obtenerGuardiasPorFecha(LocalDate fecha);

	Integer obtenerPuntosTotalesPorProfesor(Long idProfesor);

	void eliminarGuardia(Long idGuardia);
}
