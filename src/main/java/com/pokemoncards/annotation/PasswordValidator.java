package com.pokemoncards.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

	private static final char[] SPECIAL_CHARACTERS = { '+', '-', '=', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
			'{', '}', '[', ']', '|', ':', '\"', ';', '\'', '<', '>', '?', ',', '.', '/', '\\', '_', '~', '`' };

	private boolean isSpecialCharacter(char character) {
		for (char special : SPECIAL_CHARACTERS)
			if (special == character)
				return true;
		return false;
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
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

}