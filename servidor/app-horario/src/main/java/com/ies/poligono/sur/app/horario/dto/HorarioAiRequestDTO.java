package com.ies.poligono.sur.app.horario.dto;

public class HorarioAiRequestDTO {
    private Long idProfesor;
    private String pregunta;

    public HorarioAiRequestDTO() {}

    public Long getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(Long idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }
}
