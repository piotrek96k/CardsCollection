package com.project.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableAutoConfiguration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder authentication) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		authentication.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("SELECT USERNAME, PASSWORD, ENABLED FROM ACCOUNT WHERE USERNAME=?")
				.authoritiesByUsernameQuery("SELECT USERNAME, ROLE FROM ACCOUNT_ROLES WHERE USERNAME=?")
				.passwordEncoder(encoder);
		authentication.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("SELECT EMAIL, PASSWORD, ENABLED FROM ACCOUNT WHERE EMAIL=?")
				.authoritiesByUsernameQuery("SELECT EMAIL, ROLE FROM ACCOUNT_ROLES WHERE EMAIL=?")
				.passwordEncoder(encoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/register", "/login")
				.anonymous().and().formLogin().loginPage("/login").defaultSuccessUrl("/").and().logout()
				.logoutSuccessUrl("/").and().exceptionHandling().accessDeniedPage("/");
	}

}