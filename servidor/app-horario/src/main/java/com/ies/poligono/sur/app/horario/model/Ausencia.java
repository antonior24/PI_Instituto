package com.ies.poligono.sur.app.horario.model;

import java.time.LocalDate;

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
@Table(name = "ausencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ausencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate fecha;

	private String descripcion;

	private boolean justificada = false;

	@ManyToOne
	@JoinColumn(name = "id_horario", nullable = false)
	private Horario horario;
}