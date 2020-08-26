package com.pokemoncards.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableAutoConfiguration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder authentication) throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		authentication.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username, password, enabled from account where username=?")
				.authoritiesByUsernameQuery("select username, role_id from account_roles where username=?")
				.passwordEncoder(encoder);
		authentication.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select email, password, enabled from account where email=?")
				.authoritiesByUsernameQuery("select email, role_id from account_roles where email=?")
				.passwordEncoder(encoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/home").permitAll().antMatchers("/register").anonymous()
				.antMatchers("/buy/**", "/sell/**", "/mycards/**", "/expand/**", "/home/scroll/**", "/home/get/**").authenticated().and().formLogin().loginPage("/")
				.defaultSuccessUrl("/home").and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/home").and().exceptionHandling().accessDeniedPage("/home");
	}

}