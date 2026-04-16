package com.ies.poligono.sur.app.horario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.AuthResponse;
import com.ies.poligono.sur.app.horario.dto.PasswordRecoveryResult;
import com.ies.poligono.sur.app.horario.dto.RecuperacionPasswordDTO;
import com.ies.poligono.sur.app.horario.model.AuthenticationRequest;
import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.security.CustomUserDetailsService;
import com.ies.poligono.sur.app.horario.security.JwtService;
import com.ies.poligono.sur.app.horario.service.PasswordRecoveryService;

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
	private JwtService jwtService;

	@Autowired
	private PasswordRecoveryService passwordRecoveryService;

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
			PasswordRecoveryResult result = passwordRecoveryService.recoverPassword(dto.getCorreoRecuperacion());
			return ResponseEntity.ok(result);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(500)
					.body(new PasswordRecoveryResult(e.getMessage(), passwordRecoveryService.getEnvironmentName(), null));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new PasswordRecoveryResult(
					"Error al procesar la recuperacion de contrasena.",
					passwordRecoveryService.getEnvironmentName(),
					null));
		}
	}

}
