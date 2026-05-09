package com.ies.poligono.sur.app.horario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailServiceUnitTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private EmailService emailService;

	@Test
	void isConfiguredFalseSiDefaultFromVacio() {
		ReflectionTestUtils.setField(emailService, "defaultFrom", "");
		assertThat(emailService.isConfigured()).isFalse();
	}

	@Test
	void isConfiguredTrueSiHayMailSenderYDefaultFrom() {
		ReflectionTestUtils.setField(emailService, "defaultFrom", "no-reply@iespoligonosur.org");
		assertThat(emailService.isConfigured()).isTrue();
	}

	@Test
	void enviarCorreoLlamaAMailSender() {
		ReflectionTestUtils.setField(emailService, "defaultFrom", "no-reply@iespoligonosur.org");

		emailService.enviarCorreo("dest@x.com", "Asunto", "Contenido");

		verify(mailSender).send(any(SimpleMailMessage.class));
	}

	@Test
	void enviarCorreoLanzaRuntimeSiMailSenderFalla() {
		ReflectionTestUtils.setField(emailService, "defaultFrom", "no-reply@iespoligonosur.org");
		doThrow(new RuntimeException("smtp fail")).when(mailSender).send(any(SimpleMailMessage.class));

		assertThatThrownBy(() -> emailService.enviarCorreo("dest@x.com", "Asunto", "Contenido"))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("SMTP error");
	}

	@Test
	void sendTemporaryPasswordUsaSender() {
		ReflectionTestUtils.setField(emailService, "defaultFrom", "no-reply@iespoligonosur.org");

		emailService.sendTemporaryPassword("dest@x.com", "Temp1234");

		verify(mailSender).send(any(SimpleMailMessage.class));
	}
}
