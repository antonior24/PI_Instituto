package com.ies.poligono.sur.app.horario.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PingResponseDTO {
	private String estado;
	private String mensajeRecibido;
	private LocalDateTime horaServidor;
}
