package com.ies.poligono.sur.app.horario.service;

public interface PasswordRecoveryEmailSender {

    boolean isConfigured();

    void sendTemporaryPassword(String recipient, String temporaryPassword);
}
