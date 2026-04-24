package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarGuardiaDTO {

	@NotNull(message = "El ID del horario a cubrir es obligatorio")
	private Long idHorarioCobertura;

	@NotNull(message = "La fecha es obligatoria")
	private LocalDate fecha;

	private Long idProfesor; // Opcional: si viene de admin para registrar a otro profesor
}
