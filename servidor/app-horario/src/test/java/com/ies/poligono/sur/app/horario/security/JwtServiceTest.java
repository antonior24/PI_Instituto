package com.ies.poligono.sur.app.horario.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

	@Test
	void generateTokenIncluyeUsernameYRole() {
		JwtService jwtService = new JwtService();
		UserDetails user = new User(
				"admin@admin.com",
				"x",
				List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));

		String token = jwtService.generateToken(user);

		assertThat(jwtService.extractUsername(token)).isEqualTo("admin@admin.com");
		assertThat(jwtService.extractRole(token)).isEqualTo("ROLE_ADMINISTRADOR");
		assertThat(jwtService.validateToken(token, user)).isTrue();
	}
}

