package com.ies.poligono.sur.app.horario.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guardia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guardia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_profesor", nullable = false)
	private Profesor profesor;

	@ManyToOne
	@JoinColumn(name = "id_horario_cobertura", nullable = false)
	private Horario horarioCobertura;

	private LocalDate fecha;

	private Integer puntos;

	private LocalDateTime fechaRegistro;
}
