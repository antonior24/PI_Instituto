package com.ies.poligono.sur.app.horario.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ies.poligono.sur.app.horario.dto.PostImportacionInputDTO;
import com.ies.poligono.sur.app.horario.model.Asignatura;
import com.ies.poligono.sur.app.horario.model.Aula;
import com.ies.poligono.sur.app.horario.model.Curso;
import com.ies.poligono.sur.app.horario.model.Franja;
import com.ies.poligono.sur.app.horario.model.Horario;
import com.ies.poligono.sur.app.horario.model.Profesor;
import com.ies.poligono.sur.app.horario.model.Usuario;
import com.ies.poligono.sur.app.horario.service.AsignaturaService;
import com.ies.poligono.sur.app.horario.service.AulaService;
import com.ies.poligono.sur.app.horario.service.CursoService;
import com.ies.poligono.sur.app.horario.service.FranjaService;
import com.ies.poligono.sur.app.horario.service.HorarioService;
import com.ies.poligono.sur.app.horario.service.ProfesorService;
import com.ies.poligono.sur.app.horario.service.UsuarioService;

@Service
public class HorarioServiceProcessorImpl implements HorarioServiceProcessor {

	@Autowired
	HorarioService horarioService;

	@Autowired
	AsignaturaService asignaturaService;

	@Autowired
	CursoService cursoService;

	@Autowired
	AulaService aulaService;

	@Autowired
	ProfesorService profesorService;

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	FranjaService franjaService;

	@Override
	public void importarHorario(PostImportacionInputDTO inputDTO) {

		// decodificar el fichero
		byte[] decoded = Base64.getDecoder().decode(inputDTO.getFile());
		String decodedStr = new String(decoded, StandardCharsets.UTF_8);

		// leer el fichero línea a línea con la finalidad de obtener una List<Horario>
		insertarHorarioImportado(montarLstHorarioDesdeTxt(decodedStr));
	}

	/**
	 * Devuelva una lista de horarios dado el txt completo
	 * 
	 * @param txtHorario
	 * @return
	 */
	private List<Horario> montarLstHorarioDesdeTxt(String txtHorario) {
		List<Horario> lstHorario = new ArrayList<Horario>();

		// recorrer txt línea por línea y añadir un registro al array por cada línea
		try (BufferedReader reader = new BufferedReader(new StringReader(txtHorario))) {
			String txtFilaHorario;
			int lineNumber = 0;
			while ((txtFilaHorario = reader.readLine()) != null) {
				lineNumber++;
				Horario horario = montarRegistroDesdeFilaTxt(txtFilaHorario);
				if (horario != null) {
					lstHorario.add(horario);
				} else {
					System.err.println("Saltando línea " + lineNumber + " por errores de validación");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Total de horarios procesados correctamente: " + lstHorario.size());
		return lstHorario;
	}

	/**
	 * Crea un objeto horario dada la fila del txt
	 * 
	 * @param txtFilaHorario
	 * @return
	 */
	private Horario montarRegistroDesdeFilaTxt(String txtFilaHorario) {
		Horario horario = new Horario();

		// Limpiar la línea de caracteres de control (Windows \r\n)
		String lineaLimpia = txtFilaHorario.trim();
		String[] arrHorario = lineaLimpia.split("\t");

		// Limpiar cada campo de espacios y caracteres de control
		for (int i = 0; i < arrHorario.length; i++) {
			arrHorario[i] = arrHorario[i].trim();
		}

		Asignatura asignatura = procesarAsignatura(arrHorario[0]);
		horario.setAsignatura(asignatura);

		Curso curso = procesarCurso(arrHorario[1]);
		horario.setCurso(curso);

		Aula aula = procesarAula(arrHorario[2]);
		horario.setAula(aula);

		Profesor profesor = procesarProfesor(arrHorario[3]);
		horario.setProfesor(profesor);
		horario.setDia(arrHorario[4]);

		// Validar y convertir la franja
		String franjaStr = arrHorario[5];
		if (franjaStr == null || franjaStr.isEmpty() || !franjaStr.matches("\\d+")) {
			System.err.println("ERROR: Franja inválida en línea: " + txtFilaHorario);
			return null; // Saltar esta línea si la franja no es válida
		}
		
		Long idFranja = Long.valueOf(franjaStr);
		Franja franja = franjaService.findById(idFranja).orElse(null);
		
		if (franja == null) {
			System.err.println("ADVERTENCIA: No se encontró la franja con ID " + idFranja + " en la base de datos");
		}
		
		horario.setFranja(franja);

		return horario;
	}

	/**
	 * Recupera la asignatura o la inserta si no existe
	 * 
	 * @param nombreAsignatura
	 * @return
	 */
	private Asignatura procesarAsignatura(String nombreAsignatura) {
		Asignatura asignatura = asignaturaService.findByNombre(nombreAsignatura);
		if (asignatura == null) {
			asignatura = new Asignatura();
			asignatura.setNombre(nombreAsignatura);
			asignatura = asignaturaService.insertar(asignatura);
		}
		return asignatura;
	}

	/**
	 * Recupera el Curso o lo inserta si no existe
	 * 
	 * @param nombre
	 * @return
	 */
	private Curso procesarCurso(String nombre) {
		Curso curso = null;
		if (StringUtils.isNotBlank(nombre)) {
			curso = cursoService.findByNombre(nombre);
			if (curso == null) {
				curso = new Curso();
				curso.setNombre(nombre);
				curso = cursoService.insertar(curso);
			}
		}
		return curso;
	}

	/**
	 * Recupera el Aula o lo inserta si no existe
	 * 
	 * @param codigo
	 * @return
	 */
	private Aula procesarAula(String codigo) {
		Aula aula = null;

		if (StringUtils.isNotBlank(codigo)) {
			aula = aulaService.findByCodigo(codigo);
			if (aula == null) {
				aula = new Aula();
				aula.setCodigo(codigo);
				aula = aulaService.insertar(aula);
			}
		}

		return aula;
	}

	/**
	 * Recupera el Profesor o lo inserta si no existe
	 * 
	 * @param nombre
	 * @return
	 */
	private Profesor procesarProfesor(String nombre) {
		Profesor profesor = profesorService.findByNombre(nombre);
		if (profesor == null) {
			// asignar usuario al profesor
			Usuario usuario = new Usuario();
			String email = usuarioService.generarEmailDesdeNombre(nombre);
			System.out.println("Validación email: " + email);
			usuario.setEmail(email);
			usuario.setPassword("Pass123");
			usuario.setRol("profesor");
			usuario.setNombre(nombre);
			usuario = usuarioService.crearUsuario(usuario);

			// crear profesor
			profesor = new Profesor();
			profesor.setUsuario(usuario);
			profesor.setNombre(nombre);
			profesor = profesorService.insertar(profesor);
		}
		return profesor;
	}

	/**
	 * Borra el horario anterior e inserta el nuevo
	 * 
	 * @param lstHorario
	 */
	private void insertarHorarioImportado(List<Horario> lstHorario) {
		// borrar todos los registros del horario anterior
		horarioService.borrarTodosLosHorarios();

		// iterar lista de horarios y hacer insert por cada registro
		for (Horario horario : lstHorario) {
			horarioService.crearHorario(horario);
		}
	}

}
