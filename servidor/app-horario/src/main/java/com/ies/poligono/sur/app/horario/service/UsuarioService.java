package com.ies.poligono.sur.app.horario.service;

import java.util.List;

import com.ies.poligono.sur.app.horario.model.Usuario;

public interface UsuarioService {

	public List<Usuario> obtenerUsuarios();

	public Usuario crearUsuario(Usuario usuario);

	public void eliminarUsuario(Long id);

	public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado);

	public Usuario actualizarContraseña(Long id, String nuevaContraseña);

	public String generarEmailDesdeNombre(String nombre);

}
