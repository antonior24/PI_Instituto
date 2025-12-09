package com.ies.poligono.sur.app.horario.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ies.poligono.sur.app.horario.dao.ProfesorRepository;
import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.model.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	ProfesorRepository profesorRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	private final String DOMINIO_CORREO = "@iespoligonosur.org";

	@Override
	public List<Usuario> obtenerUsuarios() {
		return usuarioRepository.findAll();
	}

	@Override
	public Usuario crearUsuario(Usuario usuario) {

		if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
			throw new IllegalArgumentException("Ya existe un usuario con ese email");
		}

		String contraseñaEncriptada = passwordEncoder.encode(usuario.getPassword());
		usuario.setPassword(contraseñaEncriptada);

		return usuarioRepository.save(usuario);
	}

	@Override
	public void eliminarUsuario(Long id) {
		Usuario usuario = usuarioRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));

		// Buscar si hay un profesor asociado a este usuario
		Profesor profesor = profesorRepository.findByUsuario(usuario);
		if (profesor != null) {
			// Romper la relación antes de borrar al usuario
			profesor.setUsuario(null);
			profesorRepository.save(profesor); // guarda el cambio en la BD
		}

		// Ahora que está libre, eliminamos el usuario
		usuarioRepository.delete(usuario);
	}

	// Método para actualizar un usuario
	public Usuario actualizarUsuario(Long id_usuario, Usuario usuarioActualizado) {
		// Verificar si el usuario existe en la base de datos
		Optional<Usuario> usuarioExistenteOpt = usuarioRepository.findById(id_usuario);

		if (usuarioExistenteOpt.isPresent()) {
			Usuario usuarioExistente = usuarioExistenteOpt.get();

			// Verificar si el nuevo correo electrónico ya existe y no pertenece al usuario
			// actual
			if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())
					&& usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
				throw new RuntimeException("El correo electrónico ya está registrado");
			}

			// Si la contraseña no está vacía, se encripta
			if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
				usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
			}

			// Actualizar otros campos del usuario
			usuarioExistente.setNombre(usuarioActualizado.getNombre());
			usuarioExistente.setEmail(usuarioActualizado.getEmail());
			usuarioExistente.setRol(usuarioActualizado.getRol());

			// Guardar el usuario actualizado en la base de datos
			return usuarioRepository.save(usuarioExistente);
		} else {
			// Si no se encuentra el usuario, retornamos null
			return null;
		}
	}

	@Override
	public Usuario actualizarContraseña(Long id, String nuevaContraseña) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

		String nuevaPass = passwordEncoder.encode(nuevaContraseña);
		usuario.setPassword(nuevaPass);
		return usuarioRepository.save(usuario);
	}

	@Override
	public String generarEmailDesdeNombre(String nombre) {
		String[] arrNombre = nombre.split(",");
		String apellidos = arrNombre[0].replace(".", "").trim(); // Trovato .
		String nombreProf = "";
		if (arrNombre.length > 1) {
			nombreProf = arrNombre[1].trim().replace(".", "").concat(".");
		}
		String email = StringUtils
				.stripAccents(nombreProf.concat(apellidos).toLowerCase().replace(" ", ".").concat(DOMINIO_CORREO))
				.toLowerCase();
		return email;
	}

}
