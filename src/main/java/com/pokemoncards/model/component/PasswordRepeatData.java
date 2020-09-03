package com.pokemoncards.model.component;

import com.pokemoncards.annotation.PasswordRepeat;

@PasswordRepeat
public class PasswordRepeatData {

	private String password;

	private String passwordRepeat;

	public PasswordRepeatData() {
	}

	public PasswordRepeatData(String password, String passwordRepeat) {
		this.password = password;
		this.passwordRepeat = passwordRepeat;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	@Override
	public String toString() {
		return "PasswordRepeatData [password=" + password + ", passwordRepeat=" + passwordRepeat + "]";
	}
	
}