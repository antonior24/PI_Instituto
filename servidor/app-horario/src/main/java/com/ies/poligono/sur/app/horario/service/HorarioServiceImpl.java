package com.ies.poligono.sur.app.horario.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dao.HorarioRepository;
import com.ies.poligono.sur.app.horario.model.Horario;

@Service
public class HorarioServiceImpl implements HorarioService {

	@Autowired
	HorarioRepository horarioRepository;

	@Override
	public Horario crearHorario(Horario horario) {
		return horarioRepository.save(horario);
	}

	@Override
	public void borrarTodosLosHorarios() {
		horarioRepository.deleteAll();
	}

	@Override
	public List<Horario> obtenerTodos() {
		return horarioRepository.findAll();
	}

	@Override
	public List<Horario> obtenerPorProfesor(Long idProfesor) {
		return horarioRepository.findAll().stream()
			.filter(h -> h.getProfesor() != null && h.getProfesor().getIdProfesor().equals(idProfesor))
			.toList();
	}

}
