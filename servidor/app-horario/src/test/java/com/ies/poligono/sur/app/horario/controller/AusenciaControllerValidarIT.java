package com.ies.poligono.sur.app.horario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.ies.poligono.sur.app.horario.dto.ValidarAusenciaResponseDTO;
import com.ies.poligono.sur.app.horario.service.AusenciaService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

@SpringBootTest
@AutoConfigureMockMvc
class AusenciaControllerValidarIT {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AusenciaService ausenciaService;

	@MockBean
	private ProfesorService profesorService;

	@Test
	@WithMockUser(username = "admin@admin.com", roles = { "ADMINISTRADOR" })
	void postValidarDevuelveJsonDelServicio() throws Exception {
		LocalDate fecha = LocalDate.of(2026, 4, 27);
		ValidarAusenciaResponseDTO response = new ValidarAusenciaResponseDTO(true, "OK", fecha, "L", 2, 0);

		when(ausenciaService.validarAusencia(any(), eq(123L))).thenReturn(response);

		mockMvc.perform(post("/api/ausencias/validar")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"fecha\":\"2026-04-27\",\"idProfesor\":123}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.valido").value(true))
				.andExpect(jsonPath("$.mensaje").value("OK"))
				.andExpect(jsonPath("$.dia").value("L"))
				.andExpect(jsonPath("$.clasesEnTramo").value(2))
				.andExpect(jsonPath("$.ausenciasExistentes").value(0));
	}
}

