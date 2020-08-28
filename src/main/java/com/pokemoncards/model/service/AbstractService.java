package com.pokemoncards.model.service;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.repository.AccountRepository;

public abstract class AbstractService {
	
	@Autowired
	protected AccountRepository accountRepository;

	protected <T> T operateOnAccount(Function<AccountId, T> function, Supplier<T> supplier) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			AccountId accountId = accountRepository.getAccountId(user.getUsername());
			return function.apply(accountId);
		}
		return supplier.get();
	}
	
}
