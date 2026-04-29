package com.ies.poligono.sur.app.horario.security;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Usuario;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByEmail(email);

    if (usuario == null) {
      throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
    }

    // Convertir los roles (separados por comas) en autoridades: ROLE_ADMINISTRADOR, ROLE_PROFESOR,
    // etc.
    List<SimpleGrantedAuthority> authorities =
        Arrays.stream(usuario.getRol().split(","))
            .map(String::trim)
            .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()))
            .collect(Collectors.toList());

    return new User(usuario.getEmail(), usuario.getPassword(), authorities);
  }
}
