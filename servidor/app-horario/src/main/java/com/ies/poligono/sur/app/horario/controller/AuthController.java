package com.ies.poligono.sur.app.horario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.AuthResponse;
import com.ies.poligono.sur.app.horario.dto.RecuperacionPasswordDTO;
import com.ies.poligono.sur.app.horario.model.AuthenticationRequest;
import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.security.CustomUserDetailsService;
import com.ies.poligono.sur.app.horario.security.JwtService;

@RestController
@RequestMapping("/api")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/login")
	public AuthResponse loginUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
	    // Autenticar al usuario con el AuthenticationManager
	    authenticationManager.authenticate(
	        new UsernamePasswordAuthenticationToken(
	            authenticationRequest.getUsername(),
	            authenticationRequest.getPassword()
	        )
	    );

	    // Cargar detalles del usuario (Spring Security)
	    final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

	    // Generar el token JWT
	    final String jwt = jwtService.generateToken(userDetails);

	    // Buscar al usuario completo desde base de datos
	    Usuario usuario = usuarioRepository.findByEmail(authenticationRequest.getUsername());

	    // Devolver token + objeto Usuario (DTO)
	    return new AuthResponse(jwt, usuario);
	}

	@PostMapping("/recuperacion-password")
	public ResponseEntity<?> recuperacionPassword(@RequestBody RecuperacionPasswordDTO dto) {
		try {
			// Buscar al usuario por email
			Usuario usuario = usuarioRepository.findByEmail(dto.getCorreoRecuperacion());
			
			if (usuario == null) {
				return ResponseEntity.status(404).body("El correo no está registrado en el sistema");
			}
			
			// Generar una contraseña temporal
			String contraseñaTemporal = generarContraseñaTemporal();
			String contraseñaEncriptada = passwordEncoder.encode(contraseñaTemporal);
			
			// Actualizar la contraseña del usuario
			usuario.setPassword(contraseñaEncriptada);
			usuarioRepository.save(usuario);
			
			// En un caso real, aquí se enviaría un email con la contraseña temporal
			// Por ahora, devolveremos la contraseña temporal directamente (solo para testing)
			return ResponseEntity.ok("Contraseña temporal generada: " + contraseñaTemporal + 
				" (En producción, se enviaría por correo)");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error al procesar la recuperación: " + e.getMessage());
		}
	}
	
	// Método para generar una contraseña temporal
	private String generarContraseñaTemporal() {
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder temporal = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			temporal.append(caracteres.charAt((int) (Math.random() * caracteres.length())));
		}
		return temporal.toString();
	}


}
