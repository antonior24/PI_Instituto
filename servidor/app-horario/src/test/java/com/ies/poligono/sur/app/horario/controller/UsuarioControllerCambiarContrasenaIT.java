package com.ies.poligono.sur.app.horario.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Usuario;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerCambiarContrasenaIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	private Usuario profesor;

	@BeforeEach
	void setUp() {
		usuarioRepository.deleteAll();

		profesor = new Usuario();
		profesor.setNombre("Profe");
		profesor.setEmail("profe@iespoligonosur.org");
		profesor.setPassword("123456");
		profesor.setRol("profesor");
		profesor = usuarioRepository.save(profesor);
	}

	@Test
	@WithMockUser(username = "profe@iespoligonosur.org", roles = { "PROFESOR" })
	void profesorPuedeCambiarSuContrasena() throws Exception {
		mockMvc.perform(put("/api/usuarios/{id}/cambiar-Contrasena", profesor.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nuevaContrasena\":\"Nueva1234\"}"))
				.andExpect(status().isOk());

		Usuario recargado = usuarioRepository.findById(profesor.getId()).orElseThrow();
		assertThat(recargado.getPassword()).isNotEqualTo("antigua");
	}

	@Test
	@WithMockUser(username = "profe@iespoligonosur.org", roles = { "PROFESOR" })
	void profesorNoPuedeCambiarContrasenaDeOtroUsuario() throws Exception {
		Usuario otro = new Usuario();
		otro.setNombre("Otro");
		otro.setEmail("otro@iespoligonosur.org");
		otro.setPassword("123456");
		otro.setRol("profesor");
		otro = usuarioRepository.save(otro);

		mockMvc.perform(put("/api/usuarios/{id}/cambiar-Contrasena", otro.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nuevaContrasena\":\"Nueva1234\"}"))
				.andExpect(status().isForbidden());
	}
}
