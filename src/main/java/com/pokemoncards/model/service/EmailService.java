package com.pokemoncards.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public static final String ADMIN_EMAIL;

	@Autowired
	private JavaMailSender emailSender;

	static {
		ADMIN_EMAIL = "PokemonCardsAdm@gmail.com";
	}

	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(ADMIN_EMAIL);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);
	}

}
