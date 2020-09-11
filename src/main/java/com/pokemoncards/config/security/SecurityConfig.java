package com.pokemoncards.config.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.pokemoncards.model.repository.account.AccountRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AccountRepository accountRepository;

	@Bean
	public SessionRegistry getSessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public Map<UserDetails, Object> getUsersLockers() {
		return new HashMap<UserDetails, Object>();
	}

	@Bean
	public SecurityUserDetails getSecurityUserDetails() {
		return new SecurityUserDetails(accountRepository, getUsersLockers());
	}

	@Bean
	public HttpSessionListener getHttpSessionListener() {
		return new SessionListener();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authentication) throws Exception {
		authentication.userDetailsService(getSecurityUserDetails()).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/register").anonymous()
				.antMatchers("/buy/**", "/sell/**", "/mycards/**", "/expand/**", "/scroll/**", "/order/**", "/sort/**",
						"/home/get/**", "/home/collect/**")
				.authenticated().anyRequest().permitAll().and().formLogin().loginPage("/home")
				.defaultSuccessUrl("/home").and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/home").and().rememberMe().key("pokemonCardsKey").and().exceptionHandling()
				.accessDeniedPage("/home").and().requiresChannel().anyRequest().requiresSecure().and()
				.sessionManagement().invalidSessionUrl("/home").sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.maximumSessions(Integer.MAX_VALUE).sessionRegistry(getSessionRegistry());
	}

}