package com.ies.poligono.sur.app.horario.dao;

import com.ies.poligono.sur.app.horario.model.Franja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranjaRepository extends JpaRepository<Franja, Long> {
}

