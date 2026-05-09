package com.ies.poligono.sur.app.horario.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.ies.poligono.sur.app.horario.dao.UsuarioImagenRepository;
import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Usuario;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioImagenControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioImagenRepository usuarioImagenRepository;

	private Usuario profesor;
	private Usuario admin;

	@BeforeEach
	void setUp() {
		usuarioImagenRepository.deleteAll();
		usuarioRepository.deleteAll();

		profesor = new Usuario();
		profesor.setNombre("Profe");
		profesor.setEmail("profe@iespoligonosur.org");
		profesor.setPassword("123456");
		profesor.setRol("profesor");
		profesor = usuarioRepository.save(profesor);

		admin = new Usuario();
		admin.setNombre("Admin");
		admin.setEmail("admin@admin.com");
		admin.setPassword("123456");
		admin.setRol("administrador");
		admin = usuarioRepository.save(admin);
	}

	@Test
	@WithMockUser(username = "profe@iespoligonosur.org", roles = { "PROFESOR" })
	void profesorPuedeSubirYLeerSuImagen() throws Exception {
		byte[] bytes = new byte[] { 1, 2, 3, 4 };
		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", bytes);

		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andExpect(status().isOk());

		MvcResult result = mockMvc.perform(get("/api/usuarios/{id}/imagen", profesor.getId()))
				.andExpect(status().isOk())
				.andReturn();

		assertThat(result.getResponse().getContentType()).isEqualTo("image/png");
		assertThat(result.getResponse().getContentAsByteArray()).isEqualTo(bytes);
	}

	@Test
	@WithMockUser(username = "profe@iespoligonosur.org", roles = { "PROFESOR" })
	void profesorNoPuedeSubirNiVerImagenDeOtroUsuario() throws Exception {
		byte[] bytes = new byte[] { 9, 9, 9 };
		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", bytes);

		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", admin.getId()).file(file))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/usuarios/{id}/imagen", admin.getId()))
				.andExpect(status().isForbidden());

		mockMvc.perform(delete("/api/usuarios/{id}/imagen", admin.getId()))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void adminPuedeCambiarImagenDeOtroUsuarioYLeerla() throws Exception {
		byte[] bytes = new byte[] { 7, 8, 9 };
		MockMultipartFile file = new MockMultipartFile("imagen", "a.jpg", "image/jpeg", bytes);

		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andExpect(status().isOk());

		MvcResult result = mockMvc.perform(get("/api/usuarios/{id}/imagen", profesor.getId()))
				.andExpect(status().isOk())
				.andReturn();

		assertThat(result.getResponse().getContentType()).isEqualTo("image/jpeg");
		assertThat(result.getResponse().getContentAsByteArray()).isEqualTo(bytes);
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void getSinImagenDevuelve404() throws Exception {
		mockMvc.perform(get("/api/usuarios/{id}/imagen", profesor.getId()))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void deleteEliminaImagenYMarcaFlag() throws Exception {
		byte[] bytes = new byte[] { 1, 1, 1 };
		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", bytes);

		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andExpect(status().isOk());

		Usuario trasSubida = usuarioRepository.findById(profesor.getId()).orElseThrow();
		assertThat(trasSubida.isImagen()).isTrue();

		mockMvc.perform(delete("/api/usuarios/{id}/imagen", profesor.getId()))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/usuarios/{id}/imagen", profesor.getId()))
				.andExpect(status().isNotFound());

		Usuario recargado = usuarioRepository.findById(profesor.getId()).orElseThrow();
		assertThat(recargado.isImagen()).isFalse();
	}

	@Test
	void getSinAutenticacionDevuelve401o403() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/usuarios/{id}/imagen", profesor.getId()))
				.andReturn();
		int status = result.getResponse().getStatus();
		assertThat(status).isIn(401, 403);
	}

	@Test
	void postSinAutenticacionDevuelve401o403() throws Exception {
		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", new byte[] { 1 });

		MvcResult result = mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andReturn();
		int status = result.getResponse().getStatus();
		assertThat(status).isIn(401, 403);
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void postArchivoVacioDevuelve400() throws Exception {
		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", new byte[0]);
		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void postContentTypeNoImagenDevuelve400() throws Exception {
		MockMultipartFile file = new MockMultipartFile("imagen", "a.pdf", "application/pdf", new byte[] { 1, 2 });
		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void postMasDe2MBDevuelve413() throws Exception {
		byte[] bytes = new byte[(2 * 1024 * 1024) + 1];
		MockMultipartFile file = new MockMultipartFile("imagen", "a.jpg", "image/jpeg", bytes);
		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", profesor.getId()).file(file))
				.andExpect(status().isPayloadTooLarge());
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void postUsuarioInexistenteDevuelve404() throws Exception {
		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", new byte[] { 1 });
		mockMvc.perform(multipart("/api/usuarios/{id}/imagen", 999999L).file(file))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void deleteSinImagenEsIdempotenteDevuelve200YDejaFlagFalse() throws Exception {
		Usuario objetivo = usuarioRepository.findById(profesor.getId()).orElseThrow();
		objetivo.setImagen(true);
		usuarioRepository.save(objetivo);

		mockMvc.perform(delete("/api/usuarios/{id}/imagen", profesor.getId()))
				.andExpect(status().isOk());

		Usuario recargado = usuarioRepository.findById(profesor.getId()).orElseThrow();
		assertThat(recargado.isImagen()).isFalse();
	}
}
