package com.ies.poligono.sur.app.horario.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ies.poligono.sur.app.horario.dao.UsuarioImagenRepository;
import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.model.UsuarioImagen;

@ExtendWith(MockitoExtension.class)
class UsuarioImagenControllerUnitTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private UsuarioImagenRepository usuarioImagenRepository;

	@InjectMocks
	private UsuarioController usuarioController;

	private static UsernamePasswordAuthenticationToken authProfesor(String email) {
		return new UsernamePasswordAuthenticationToken(
				email,
				"N/A",
				List.of(new SimpleGrantedAuthority("ROLE_PROFESOR")));
	}

	private static UsernamePasswordAuthenticationToken authAdmin(String email) {
		return new UsernamePasswordAuthenticationToken(
				email,
				"N/A",
				List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
	}

	@Test
	void profesorNoPuedeSubirImagenDeOtroUsuario() {
		Usuario logueado = new Usuario();
		logueado.setId(10L);
		logueado.setEmail("profe@iespoligonosur.org");

		when(usuarioRepository.findByEmail("profe@iespoligonosur.org")).thenReturn(logueado);

		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", new byte[] { 1 });

		ResponseEntity<?> response = usuarioController.subirImagen(99L, file, authProfesor("profe@iespoligonosur.org"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		verify(usuarioImagenRepository, never()).save(any());
		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void adminPuedeSubirImagenDeOtroUsuarioYMarcaFlag() {
		Usuario admin = new Usuario();
		admin.setId(1L);
		admin.setEmail("admin@admin.com");

		Usuario objetivo = new Usuario();
		objetivo.setId(50L);
		objetivo.setEmail("otro@iespoligonosur.org");
		objetivo.setImagen(false);

		when(usuarioRepository.findByEmail("admin@admin.com")).thenReturn(admin);
		when(usuarioRepository.findById(50L)).thenReturn(Optional.of(objetivo));
		when(usuarioImagenRepository.save(any(UsuarioImagen.class))).thenAnswer(inv -> inv.getArgument(0));
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

		MockMultipartFile file = new MockMultipartFile("imagen", "a.png", "image/png", new byte[] { 1, 2, 3 });

		ResponseEntity<?> response = usuarioController.subirImagen(50L, file, authAdmin("admin@admin.com"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(objetivo.isImagen()).isTrue();
		verify(usuarioImagenRepository).save(any(UsuarioImagen.class));
		verify(usuarioRepository).save(eq(objetivo));
	}

	@Test
	void subirImagenRechazaContentTypeNulo() {
		Usuario admin = new Usuario();
		admin.setId(1L);
		admin.setEmail("admin@admin.com");

		Usuario objetivo = new Usuario();
		objetivo.setId(50L);

		when(usuarioRepository.findByEmail("admin@admin.com")).thenReturn(admin);

		MockMultipartFile file = new MockMultipartFile("imagen", "a.bin", null, new byte[] { 1 });

		ResponseEntity<?> response = usuarioController.subirImagen(50L, file, authAdmin("admin@admin.com"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		verify(usuarioImagenRepository, never()).save(any());
	}

	@Test
	void subirImagenRechazaMasDe2MB() {
		Usuario admin = new Usuario();
		admin.setId(1L);
		admin.setEmail("admin@admin.com");

		Usuario objetivo = new Usuario();
		objetivo.setId(50L);

		when(usuarioRepository.findByEmail("admin@admin.com")).thenReturn(admin);

		byte[] bytes = new byte[(2 * 1024 * 1024) + 1];
		MockMultipartFile file = new MockMultipartFile("imagen", "a.jpg", "image/jpeg", bytes);

		ResponseEntity<?> response = usuarioController.subirImagen(50L, file, authAdmin("admin@admin.com"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
		verify(usuarioImagenRepository, never()).save(any());
	}

	@Test
	void obtenerImagenDevuelve404SiNoExiste() {
		Usuario logueado = new Usuario();
		logueado.setId(10L);
		logueado.setEmail("profe@iespoligonosur.org");

		when(usuarioRepository.findByEmail("profe@iespoligonosur.org")).thenReturn(logueado);
		when(usuarioImagenRepository.findById(10L)).thenReturn(Optional.empty());

		ResponseEntity<?> response = usuarioController.obtenerImagen(10L, authProfesor("profe@iespoligonosur.org"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void borrarImagenEsIdempotenteSiNoExisteRegistro() {
		Usuario admin = new Usuario();
		admin.setId(1L);
		admin.setEmail("admin@admin.com");

		Usuario objetivo = new Usuario();
		objetivo.setId(50L);
		objetivo.setEmail("otro@iespoligonosur.org");
		objetivo.setImagen(true);

		when(usuarioRepository.findByEmail("admin@admin.com")).thenReturn(admin);
		when(usuarioRepository.findById(50L)).thenReturn(Optional.of(objetivo));
		when(usuarioImagenRepository.findById(50L)).thenReturn(Optional.empty());
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

		ResponseEntity<?> response = usuarioController.borrarImagen(50L, authAdmin("admin@admin.com"));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(objetivo.isImagen()).isFalse();
		verify(usuarioImagenRepository, never()).delete(any());
		verify(usuarioRepository).save(eq(objetivo));
	}
}
