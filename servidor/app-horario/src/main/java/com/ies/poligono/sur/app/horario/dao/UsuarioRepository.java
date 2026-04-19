package com.ies.poligono.sur.app.horario.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ies.poligono.sur.app.horario.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
	Usuario findByEmail(@Param("email") String email);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
	boolean existsByEmail(@Param("email") String email);

}
