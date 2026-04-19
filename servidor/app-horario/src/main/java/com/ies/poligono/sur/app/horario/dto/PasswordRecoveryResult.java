package com.ies.poligono.sur.app.horario.dto;

public class PasswordRecoveryResult {

    private final String mensaje;
    private final String ambiente;
    private final String contrasenaTemporal;

    public PasswordRecoveryResult(String mensaje, String ambiente, String contrasenaTemporal) {
        this.mensaje = mensaje;
        this.ambiente = ambiente;
        this.contrasenaTemporal = contrasenaTemporal;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public String getContrasenaTemporal() {
        return contrasenaTemporal;
    }

    public boolean hasTemporaryPassword() {
        return contrasenaTemporal != null && !contrasenaTemporal.isBlank();
    }
}
