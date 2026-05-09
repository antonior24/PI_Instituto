package com.ies.poligono.sur.app.horario.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Usuario;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void loadUserByUsernameLanzaSiNoExiste() {
		when(usuarioRepository.findByEmail("no@iespoligonosur.org")).thenReturn(null);

		assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("no@iespoligonosur.org"))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("Usuario no encontrado");
	}

	@Test
	void loadUserByUsernameDevuelveAuthoritiesConROLE() {
		Usuario usuario = new Usuario();
		usuario.setEmail("profe@iespoligonosur.org");
		usuario.setPassword("ENC");
		usuario.setRol("profesor");

		when(usuarioRepository.findByEmail("profe@iespoligonosur.org")).thenReturn(usuario);

		UserDetails details = customUserDetailsService.loadUserByUsername("profe@iespoligonosur.org");

		assertThat(details.getUsername()).isEqualTo("profe@iespoligonosur.org");
		assertThat(details.getPassword()).isEqualTo("ENC");
		assertThat(details.getAuthorities())
				.extracting(a -> a.getAuthority())
				.containsExactly("ROLE_PROFESOR");
	}
}
