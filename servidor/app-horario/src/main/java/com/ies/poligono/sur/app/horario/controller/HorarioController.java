package com.ies.poligono.sur.app.horario.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.dto.PostImportacionInputDTO;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.processor.HorarioServiceProcessor;
import com.ies.poligono.sur.app.horario.service.HorarioService;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

	@Autowired
	HorarioServiceProcessor horarioServiceProcessor;
	
	@Autowired
	HorarioService horarioService;

	// Endpoint para obtener horarios
	@GetMapping
	public ResponseEntity<List<Horario>> obtenerHorarios(@RequestParam(required = false) Long idProfesor) {
		List<Horario> horarios;
		if (idProfesor != null) {
			horarios = horarioService.obtenerPorProfesor(idProfesor);
		} else {
			horarios = horarioService.obtenerTodos();
		}
		return ResponseEntity.ok(horarios);
	}

	// Endpoint para subir el archivo
	@PostMapping("/importacion")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Map<String, String>> importacion(@RequestBody PostImportacionInputDTO inputDTO) {
		horarioServiceProcessor.importarHorario(inputDTO);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Horarios importados correctamente");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
