package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class CardsCollectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsCollectionApplication.class, args);
	}
	
}