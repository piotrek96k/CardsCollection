package com.pokemoncards.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.pokemoncards.model.repository.AccountRepository;

@Configuration
@EnableAutoConfiguration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	protected void configure(AuthenticationManagerBuilder authentication) throws Exception {
		authentication.userDetailsService(new SecurityUserDetails(accountRepository))
				.passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/register").anonymous()
				.antMatchers("/buy/**", "/sell/**", "/mycards/**", "/expand/**", "/home/get/**", "/home/collect/**")
				.authenticated().anyRequest().permitAll().and().formLogin().loginPage("/home")
				.defaultSuccessUrl("/home").and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/home").and().rememberMe().key("pokemonCardsKey").and().exceptionHandling()
				.accessDeniedPage("/home").and().requiresChannel().anyRequest().requiresSecure();
	}

}