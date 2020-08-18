package com.pokemoncards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class PokemonCardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokemonCardsApplication.class, args);
	}
	
}