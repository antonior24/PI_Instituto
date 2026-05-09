package com.ies.poligono.sur.app.horario.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.model.UsuarioImagen;

@DataJpaTest
class UsuarioImagenRepositoryTest {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioImagenRepository usuarioImagenRepository;

	@Test
	void guardaYRecuperaImagenPorIdUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNombre("Profe");
		usuario.setEmail("profe@iespoligonosur.org");
		usuario.setPassword("123456");
		usuario.setRol("profesor");
		usuario = usuarioRepository.save(usuario);

		byte[] datos = new byte[] { 1, 2, 3 };
		UsuarioImagen img = new UsuarioImagen(usuario.getId(), "image/png", datos);
		usuarioImagenRepository.save(img);

		UsuarioImagen recuperada = usuarioImagenRepository.findById(usuario.getId()).orElseThrow();
		assertThat(recuperada.getMimeType()).isEqualTo("image/png");
		assertThat(recuperada.getDatos()).isEqualTo(datos);
	}
}
