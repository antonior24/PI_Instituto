package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.ies.poligono.sur.app.horario.dao.ProfesorRepository;
import com.ies.poligono.sur.app.horario.dao.UsuarioImagenRepository;
import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.model.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplUnitTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private ProfesorRepository profesorRepository;

	@Mock
	private UsuarioImagenRepository usuarioImagenRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UsuarioServiceImpl usuarioService;

	@Test
	void eliminarUsuarioRompeRelacionProfesorYEliminaImagen() {
		Usuario usuario = new Usuario();
		usuario.setId(5L);

		Profesor profesor = new Profesor();
		profesor.setIdProfesor(1L);
		profesor.setUsuario(usuario);

		when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
		when(profesorRepository.findByUsuario(usuario)).thenReturn(profesor);

		usuarioService.eliminarUsuario(5L);

		ArgumentCaptor<Profesor> profesorCaptor = ArgumentCaptor.forClass(Profesor.class);
		verify(profesorRepository).save(profesorCaptor.capture());
		assertThat(profesorCaptor.getValue().getUsuario()).isNull();

		verify(usuarioImagenRepository).deleteById(5L);
		verify(usuarioRepository).delete(usuario);
	}

	@Test
	void eliminarUsuarioDevuelve404SiNoExiste() {
		when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> usuarioService.eliminarUsuario(99L))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(ex -> {
					ResponseStatusException rse = (ResponseStatusException) ex;
					assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
				});

		verify(usuarioRepository).findById(99L);
	}

	@Test
	void crearUsuarioEncriptaContrasena() {
		Usuario usuario = new Usuario();
		usuario.setEmail("nuevo@iespoligonosur.org");
		usuario.setPassword("123456");

		when(usuarioRepository.findByEmail("nuevo@iespoligonosur.org")).thenReturn(null);
		when(passwordEncoder.encode("123456")).thenReturn("ENC");
		when(usuarioRepository.save(usuario)).thenReturn(usuario);

		Usuario creado = usuarioService.crearUsuario(usuario);

		assertThat(creado.getPassword()).isEqualTo("ENC");
		verify(usuarioRepository).save(usuario);
	}

	@Test
	void crearUsuarioFallaSiEmailYaExiste() {
		Usuario usuario = new Usuario();
		usuario.setEmail("existente@iespoligonosur.org");
		usuario.setPassword("123456");

		when(usuarioRepository.findByEmail("existente@iespoligonosur.org")).thenReturn(new Usuario());

		assertThatThrownBy(() -> usuarioService.crearUsuario(usuario))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Ya existe un usuario con ese email");
	}

	@Test
	void actualizarUsuarioEncriptaSiPasswordNoVaciaYActualizaCampos() {
		Usuario existente = new Usuario();
		existente.setId(1L);
		existente.setNombre("Anterior");
		existente.setEmail("a@iespoligonosur.org");
		existente.setPassword("OLD");
		existente.setRol("profesor");

		Usuario cambios = new Usuario();
		cambios.setNombre("Nuevo Nombre");
		cambios.setEmail("nuevo@iespoligonosur.org");
		cambios.setPassword("123456");
		cambios.setRol("administrador");

		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(usuarioRepository.existsByEmail("nuevo@iespoligonosur.org")).thenReturn(false);
		when(passwordEncoder.encode("123456")).thenReturn("ENC");
		when(usuarioRepository.save(existente)).thenReturn(existente);

		Usuario actualizado = usuarioService.actualizarUsuario(1L, cambios);

		assertThat(actualizado.getNombre()).isEqualTo("Nuevo Nombre");
		assertThat(actualizado.getEmail()).isEqualTo("nuevo@iespoligonosur.org");
		assertThat(actualizado.getRol()).isEqualTo("administrador");
		assertThat(actualizado.getPassword()).isEqualTo("ENC");
	}

	@Test
	void actualizarUsuarioMantienePasswordSiVacia() {
		Usuario existente = new Usuario();
		existente.setId(1L);
		existente.setEmail("a@iespoligonosur.org");
		existente.setPassword("OLD");

		Usuario cambios = new Usuario();
		cambios.setNombre("X");
		cambios.setEmail("a@iespoligonosur.org");
		cambios.setPassword("");
		cambios.setRol("profesor");

		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(usuarioRepository.save(existente)).thenReturn(existente);

		Usuario actualizado = usuarioService.actualizarUsuario(1L, cambios);

		assertThat(actualizado.getPassword()).isEqualTo("OLD");
	}

	@Test
	void actualizarUsuarioFallaSiEmailYaRegistradoEnOtroUsuario() {
		Usuario existente = new Usuario();
		existente.setId(1L);
		existente.setEmail("a@iespoligonosur.org");

		Usuario cambios = new Usuario();
		cambios.setEmail("otro@iespoligonosur.org");

		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(usuarioRepository.existsByEmail("otro@iespoligonosur.org")).thenReturn(true);

		assertThatThrownBy(() -> usuarioService.actualizarUsuario(1L, cambios))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("El correo electr\u00f3nico ya est\u00e1 registrado");
	}

	@Test
	void actualizarContrasenaEncriptaYGuarda() {
		Usuario existente = new Usuario();
		existente.setId(1L);
		existente.setPassword("OLD");

		when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(passwordEncoder.encode("Nueva123")).thenReturn("ENC");
		when(usuarioRepository.save(existente)).thenReturn(existente);

		Usuario actualizado = usuarioService.actualizarContrase\u00f1a(1L, "Nueva123");

		assertThat(actualizado.getPassword()).isEqualTo("ENC");
	}

	@Test
	void actualizarContrasena404SiNoExiste() {
		when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> usuarioService.actualizarContrase\u00f1a(1L, "Nueva123"))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
	}

	@Test
	void generarEmailDesdeNombreNormalizaAcentosYEspacios() {
		String email = usuarioService.generarEmailDesdeNombre("Garc\u00eda M\u00e1rquez, Jos\u00e9");
		assertThat(email).isEqualTo("jose.garcia.marquez@iespoligonosur.org");
	}

	@Test
	void generarEmailDesdeNombreSinComa() {
		String email = usuarioService.generarEmailDesdeNombre("Apellido");
		assertThat(email).isEqualTo("apellido@iespoligonosur.org");
	}
}

