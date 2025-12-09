package com.ies.poligono.sur.app.horario.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.Aula;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

	Aula findByCodigo(String codigo);

}