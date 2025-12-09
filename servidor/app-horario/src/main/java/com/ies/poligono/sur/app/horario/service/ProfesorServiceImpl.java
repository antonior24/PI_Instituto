package com.ies.poligono.sur.app.horario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dao.ProfesorRepository;
import com.ies.poligono.sur.app.horario.model.Profesor;

@Service
public class ProfesorServiceImpl implements ProfesorService {

	@Autowired
	ProfesorRepository profesorRepository;

	@Override
	public Profesor findByNombre(String nombre) {
		return profesorRepository.findByNombre(nombre);
	}
	
	public List<Profesor> buscarPorNombreParcial(String nombre) {
	    return profesorRepository.findByNombreContainingIgnoreCase(nombre);
	}

	@Override
	public Profesor findById(Long id) {
		return profesorRepository.findById(id).orElse(null);
	}
	
	@Override
    public List<Profesor> obtenerTodos() {
        return profesorRepository.findAll();
    }
	
	@Override
	public Profesor insertar(Profesor profesor) {
	    return profesorRepository.save(profesor);
	}
	
	@Override
	public Profesor findByEmailUsuario(String email) {
	    return profesorRepository.findByUsuarioEmail(email)
	            .orElseThrow(() -> new RuntimeException("Profesor no encontrado para el email: " + email));
	}
	
	@Override
    public Long obtenerIdProfesorPorUsername(String email) {
        Optional<Profesor> profesor = profesorRepository.findByUsuario_Email(email);
        
        if (profesor.isEmpty()) {
            System.out.println("→ No se encontró ningún profesor para el email: " + email);
            return null;
        }

        Long id = profesor.get().getIdProfesor(); // o getId() según tu modelo
        System.out.println("→ ID del profesor encontrado: " + id);
        return id;
    }

	@Override
	public Profesor findByIdUsuario(Long idUsuario) {
		return profesorRepository.findByUsuarioId(idUsuario)
				.orElse(null);
	}

}
