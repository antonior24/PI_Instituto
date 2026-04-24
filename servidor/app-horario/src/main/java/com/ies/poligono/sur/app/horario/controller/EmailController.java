package com.ies.poligono.sur.app.horario.controller;

import com.ies.poligono.sur.app.horario.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  private final EmailService emailService;

  public EmailController(EmailService emailService) {
    this.emailService = emailService;
  }

  @GetMapping("/test-email")
  public String testEmail() {
    emailService.enviarCorreo(
        "test@iespoligonosur.org",
        "Test Mailpit",
        "Prueba desde EmailController via SMTP. Ver http://localhost:8025");
    return "Correo enviado a Mailpit (localhost:8025)";
  }
}
