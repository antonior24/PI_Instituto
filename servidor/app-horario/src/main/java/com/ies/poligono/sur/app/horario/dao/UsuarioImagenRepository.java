package com.ies.poligono.sur.app.horario.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.UsuarioImagen;

@Repository
public interface UsuarioImagenRepository extends JpaRepository<UsuarioImagen, Long> {
}

