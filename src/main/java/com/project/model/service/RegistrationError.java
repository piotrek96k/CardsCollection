package com.project.model.service;

public enum RegistrationError {
	
	USERNAME_TOO_SHORT("usernameTooShort"),
	
	PASSWORD_TOO_SHORT("passwordTooShort"),

	USERNAME_EXISTS("usernameExists"),

	EMAIL_EXISTS("emailExists"),
	
	DIFFERENT_PASSWORDS("differentPasswords"),
	
	WRONG_PASSWORD("wrongPassword");

	private String error;

	private RegistrationError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

}
