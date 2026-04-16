package com.ies.poligono.sur.app.horario.dto;

public class PasswordRecoveryResult {

    private final String message;
    private final String temporaryPassword;

    public PasswordRecoveryResult(String message, String temporaryPassword) {
        this.message = message;
        this.temporaryPassword = temporaryPassword;
    }

    public String getMessage() {
        return message;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public boolean hasTemporaryPassword() {
        return temporaryPassword != null && !temporaryPassword.isBlank();
    }
}
