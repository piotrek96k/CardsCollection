package com.project.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.model.entity.Account;
import com.project.model.entity.AccountId;
import com.project.model.entity.Cards;
import com.project.model.entity.Role;
import com.project.model.entity.RoleEnum;
import com.project.model.repository.AccountRepository;
import com.project.model.repository.RoleRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordChecker passwordChecker;

	public Optional<RegistrationError> addAccount(Account account, String pswRepeat) {
		if (account.getUsername().length() < 4)
			return Optional.of(RegistrationError.USERNAME_TOO_SHORT);
		if (account.getPassword().length() < 8)
			return Optional.of(RegistrationError.PASSWORD_TOO_SHORT);
		if (!account.getPassword().equals(pswRepeat))
			return Optional.of(RegistrationError.DIFFERENT_PASSWORDS);
		if (!passwordChecker.validatePassword(pswRepeat))
			return Optional.of(RegistrationError.WRONG_PASSWORD);
		if (accountRepository.findByUsername(account.getUsername()) != null)
			return Optional.of(RegistrationError.USERNAME_EXISTS);
		if (accountRepository.findByEmail(account.getEmail()) != null)
			return Optional.of(RegistrationError.EMAIL_EXISTS);
		Role role = roleRepository.getOne(RoleEnum.ROLE_USER.toString());
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
		account.setEnabled(true);
		account.setCoins(1000);
		accountRepository.save(account);
		return Optional.empty();
	}

	public List<Cards> getCards(int page) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			AccountId accountId = accountRepository.getAccountId(user.getUsername());
			return accountRepository.getAccountCardsListByPage(accountId.getUsername(), page);
		}
		return new ArrayList<Cards>();
	}
	
	public int getNumberOfPages() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			AccountId accountId = accountRepository.getAccountId(user.getUsername());
			return accountRepository.getNumberOfPages(accountId.getUsername());
		}
		return 1;
	}

}