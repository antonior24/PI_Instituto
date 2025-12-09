package com.ies.poligono.sur.app.horario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dao.AulaRepository;
import com.ies.poligono.sur.app.horario.model.Aula;

@Service
public class AulaServiceImpl implements AulaService {

	@Autowired
	AulaRepository aulaRepository;

	@Override
	public Aula findByCodigo(String codigo) {
		return aulaRepository.findByCodigo(codigo);
	}

	@Override
	public Aula insertar(Aula aula) {
		return aulaRepository.save(aula);
	}
}
