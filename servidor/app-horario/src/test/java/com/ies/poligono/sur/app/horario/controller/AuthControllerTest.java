package com.ies.poligono.sur.app.horario.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.service.PasswordRecoveryEmailSender;
import com.ies.poligono.sur.app.horario.service.PasswordRecoveryService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordRecoveryEmailSender passwordRecoveryEmailSender;

    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PasswordRecoveryService passwordRecoveryService = new PasswordRecoveryService(
                usuarioRepository,
                passwordEncoder,
                passwordRecoveryEmailSender,
                "testing");

        authController = new AuthController();
        ReflectionTestUtils.setField(authController, "passwordRecoveryService", passwordRecoveryService);

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void recuperacionPasswordReturnsStructuredJson() throws Exception {
        org.mockito.Mockito.when(passwordRecoveryEmailSender.isConfigured()).thenReturn(false);
        org.mockito.Mockito.when(usuarioRepository.findByEmail("carlos.robles.dominguez.al@iespoligonosur.org"))
                .thenReturn(crearUsuario());
        org.mockito.Mockito.when(passwordEncoder.encode(org.mockito.ArgumentMatchers.any(String.class)))
                .thenReturn("encoded-temp-password");

        mockMvc.perform(post("/api/recuperacion-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correoRecuperacion\":\"carlos.robles.dominguez.al@iespoligonosur.org\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value(
                        "Si el correo existe, se ha generado una contrasena temporal para pruebas."))
                .andExpect(jsonPath("$.ambiente").value("testing"))
                .andExpect(jsonPath("$.contrasenaTemporal").isString())
                .andExpect(jsonPath("$.contrasenaTemporal").value(org.hamcrest.Matchers.hasLength(8)));
    }

    private static Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos Robles");
        usuario.setEmail("carlos.robles.dominguez.al@iespoligonosur.org");
        usuario.setPassword("123456");
        usuario.setRol("profesor");
        return usuario;
    }
}
