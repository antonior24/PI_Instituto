package com.ies.poligono.sur.app.horario.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "franja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Franja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFranja;

    private LocalTime horaInicio;

    private LocalTime horaFin;
}