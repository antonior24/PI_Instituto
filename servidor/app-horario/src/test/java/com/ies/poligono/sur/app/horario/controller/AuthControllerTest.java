package com.ies.poligono.sur.app.horario.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ies.poligono.sur.app.horario.dao.UsuarioRepository;
import com.ies.poligono.sur.app.horario.dto.PasswordRecoveryResult;
import com.ies.poligono.sur.app.horario.security.CustomUserDetailsService;
import com.ies.poligono.sur.app.horario.security.JwtService;
import com.ies.poligono.sur.app.horario.service.PasswordRecoveryService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void recuperacionPasswordReturnsStructuredJson() throws Exception {
        when(passwordRecoveryService.recoverPassword("carlos.robles.dominguez.al@iespoligonosur.org"))
                .thenReturn(new PasswordRecoveryResult(
                        "Si el correo existe, se ha generado una contrasena temporal para pruebas.",
                        "desarrollo",
                        "Temp1234"));

        mockMvc.perform(post("/api/recuperacion-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correoRecuperacion\":\"carlos.robles.dominguez.al@iespoligonosur.org\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje").value(
                        "Si el correo existe, se ha generado una contrasena temporal para pruebas."))
                .andExpect(jsonPath("$.ambiente").value("desarrollo"))
                .andExpect(jsonPath("$.contrasenaTemporal").value("Temp1234"));
    }
}
