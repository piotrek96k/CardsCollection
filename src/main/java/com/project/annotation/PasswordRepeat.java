package com.project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordRepeatValidator.class)
public @interface PasswordRepeat {

	String message() default "Enetered passwords are different";
	
	Class<?>[] groups() default{};
	
	Class<? extends Payload>[] payload() default {};
	
}
