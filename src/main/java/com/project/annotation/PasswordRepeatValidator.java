package com.project.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.project.model.entity.PasswordRepeatData;

public class PasswordRepeatValidator implements ConstraintValidator<PasswordRepeat, PasswordRepeatData>{

	@Override
	public boolean isValid(PasswordRepeatData data, ConstraintValidatorContext context) {
		if(data.getPassword().equals(data.getPasswordRepeat()))
			return true;
		return false;
	}

}
