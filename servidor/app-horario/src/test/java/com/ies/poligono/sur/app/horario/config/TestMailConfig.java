package com.ies.poligono.sur.app.horario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Profile("test")
public class TestMailConfig {

	@Bean
	public JavaMailSender javaMailSender() {
		return new JavaMailSenderImpl();
	}
}

