package com.pokemoncards.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.pokemoncards.model.repository.account.AccountRepository;

public class UniqueValidator implements ConstraintValidator<Unique, String> {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		if (accountRepository.getAccountId(name) == null)
			return true;
		return false;
	}

}