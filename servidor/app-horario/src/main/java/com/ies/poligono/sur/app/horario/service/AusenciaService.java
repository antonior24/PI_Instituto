package com.ies.poligono.sur.app.horario.service;

import java.time.LocalDate;
import java.util.List;

import com.ies.poligono.sur.app.horario.dto.AusenciaAgrupadaDTO;
import com.ies.poligono.sur.app.horario.dto.PostAusenciasInputDTO;

public interface AusenciaService {

	void crearAusencia(PostAusenciasInputDTO dto, Long idProfesor);

	void eliminarAusenciaPorId(Long id);

	List<AusenciaAgrupadaDTO> obtenerAusenciasAgrupadas(Long idProfesor);

	void eliminarAusenciasPorFechaYProfesor(LocalDate fecha, Long idProfesor);

	void crearAusenciaV2(PostAusenciasInputDTO dto, Long idProfesor);

	List<AusenciaAgrupadaDTO> obtenerAusenciasAgrupadasV2(Long idProfesor);

	void justificarAusenciasDia(LocalDate fecha, Long idProfesor);

}
