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

import com.ies.poligono.sur.app.horario.dto.GuardiaResponseDTO;
import com.ies.poligono.sur.app.horario.dto.RegistrarGuardiaDTO;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.AusenciaService;
import com.ies.poligono.sur.app.horario.service.GuardiaService;
import com.ies.poligono.sur.app.horario.service.HorarioService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

@SpringBootTest
@AutoConfigureMockMvc
class GuardiaControllerRegistrarIT {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GuardiaService guardiaService;

	@MockBean
	private ProfesorService profesorService;

	@MockBean
	private AusenciaService ausenciaService;

	@MockBean
	private HorarioService horarioService;

	@Test
	@WithMockUser(username = "profe@iespoligonosur.org", roles = { "PROFESOR" })
	void registrarGuardiaDevuelve200ConJson() throws Exception {
		Profesor profesor = new Profesor();
		profesor.setIdProfesor(77L);
		profesor.setNombre("Profe");
		when(profesorService.findByEmailUsuario("profe@iespoligonosur.org")).thenReturn(profesor);

		LocalDate fecha = LocalDate.of(2026, 4, 27);
		GuardiaResponseDTO response = new GuardiaResponseDTO(
				1L, 77L, "Profe", 2L, "1ER ESO", fecha, 4, "Matematicas", "A1", "08:15 - 09:15");

		when(guardiaService.registrarGuardia(any(RegistrarGuardiaDTO.class), eq(77L))).thenReturn(response);

		mockMvc.perform(post("/api/guardias")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"idHorarioCobertura\":10,\"fecha\":\"2026-04-27\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idProfesor").value(77))
				.andExpect(jsonPath("$.puntos").value(4))
				.andExpect(jsonPath("$.asignatura").value("Matematicas"));
	}

	@Test
	@WithMockUser(username = "profe@iespoligonosur.org", roles = { "PROFESOR" })
	void registrarGuardiaDevuelve400SiServicioLanzaIllegalArgument() throws Exception {
		Profesor profesor = new Profesor();
		profesor.setIdProfesor(77L);
		profesor.setNombre("Profe");
		when(profesorService.findByEmailUsuario("profe@iespoligonosur.org")).thenReturn(profesor);

		when(guardiaService.registrarGuardia(any(RegistrarGuardiaDTO.class), eq(77L)))
				.thenThrow(new IllegalArgumentException("bad"));

		mockMvc.perform(post("/api/guardias")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"idHorarioCobertura\":10,\"fecha\":\"2026-04-27\"}"))
				.andExpect(status().isBadRequest());
	}
}

