package com.ies.poligono.sur.app.horario.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.dto.PingRequestDTO;
import com.ies.poligono.sur.app.horario.dto.PingResponseDTO;

@RestController
@RequestMapping("/api")
public class PingController {

	@PostMapping("/ping")
	public ResponseEntity<PingResponseDTO> ping(@RequestBody(required = false) PingRequestDTO request) {
		String mensaje = request != null ? request.getMensaje() : null;
		String estado = "pong";
		return ResponseEntity.ok(new PingResponseDTO(estado, mensaje, LocalDateTime.now()));
	}
}
