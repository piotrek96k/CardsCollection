package com.pokemoncards.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	private static final String URL;

	private static final String USERNAME;

	private static final String PASSWORD;

	static {
		URL = "jdbc:postgresql://localhost:5432/Pokemon_Cards";
		USERNAME = "postgres";
		PASSWORD = "postgres";
	}

	@Bean
	public DataSource getDataSource() {
		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.url(URL);
		dataSourceBuilder.username(USERNAME);
		dataSourceBuilder.password(PASSWORD);
		return dataSourceBuilder.build();
	}

}
