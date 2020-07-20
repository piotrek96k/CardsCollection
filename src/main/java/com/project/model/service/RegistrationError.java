package com.project.model.service;

public enum RegistrationError {

	USERNAME_EXISTS("usernameExists"),

	EMAIL_EXISTS("emailExists");

	private String error;

	private RegistrationError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

}
