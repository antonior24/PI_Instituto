package com.ies.poligono.sur.app.horario.validator;

import java.time.LocalDate;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ies.poligono.sur.app.horario.dto.PostAusenciasInputDTO;

public class AusenciaValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return PostAusenciasInputDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PostAusenciasInputDTO dto = (PostAusenciasInputDTO) target;
		// validaciones
		if (dto.getFecha().isBefore(LocalDate.now())) {
			// TODO: rellenar error
			throw new IllegalArgumentException("No se puede registrar una ausencia en el pasado.");
		}
	}

}