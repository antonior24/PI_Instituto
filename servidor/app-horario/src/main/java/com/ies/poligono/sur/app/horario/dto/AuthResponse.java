package com.ies.poligono.sur.app.horario.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ies.poligono.sur.app.horario.model.Usuario;

public class AuthResponse {
    private String token;
    private Usuario usuario;
    
    @JsonIgnore
    private String password;


    public AuthResponse(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    
    

}
