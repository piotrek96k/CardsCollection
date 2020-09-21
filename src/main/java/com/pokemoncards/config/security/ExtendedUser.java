package com.pokemoncards.config.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class ExtendedUser extends User {
	
	private static final long serialVersionUID = -5488949486539987339L;
	
	private String email;

	public ExtendedUser(String username, String email, String password, boolean enabled, List<GrantedAuthority> authorities) {
		super(username, password, enabled, true, true, true, authorities);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

}