package com.project.model.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordChecker {

	private static final char[] SPECIAL_CHARACTERS = { '+', '-', '=', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
			'{', '}', '[', ']', '|', ':', '\"', ';', '\'', '<', '>', '?', ',', '.', '/', '\\', '_','~','`' };

	public boolean validatePassword(String password) {
		int[] counter = new int[] { 0, 0, 0, 0 };
		for (int i = 0; i < password.length(); i++) {
			char character = password.charAt(i);
			if (Character.isUpperCase(character))
				counter[0]++;
			else if (Character.isLowerCase(character))
				counter[1]++;
			else if (Character.isDigit(character))
				counter[2]++;
			else if (isSpecialCharacter(character))
				counter[3]++;
			else
				return false;
		}
		for (int i : counter)
			if (i == 0)
				return false;
		return true;
	}

	private boolean isSpecialCharacter(char character) {
		for (char special : SPECIAL_CHARACTERS)
			if (special == character)
				return true;
		return false;
	}

}