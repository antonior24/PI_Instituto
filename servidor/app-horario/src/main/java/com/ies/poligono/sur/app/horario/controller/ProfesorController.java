package com.ies.poligono.sur.app.horario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.service.ProfesorService;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

	@Autowired
    private ProfesorService profesorService;

    @GetMapping("/buscar")
    public ResponseEntity<List<Profesor>> buscarPorNombre(@RequestParam("nombre") String nombre) {
        List<Profesor> profesores = profesorService.buscarPorNombreParcial(nombre);
        return ResponseEntity.ok(profesores);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Profesor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profesorService.findById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Profesor>> obtenerTodos() {
        List<Profesor> profesores = profesorService.obtenerTodos();
        return ResponseEntity.ok(profesores);
    }
    




}
