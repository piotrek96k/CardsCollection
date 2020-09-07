package com.pokemoncards.config;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class ExtendedUser extends User {
	
	private static final long serialVersionUID = -5488949486539987339L;
	
	private String email;

	public ExtendedUser(String username, String email, String password, List<GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

}
