package com.project.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.model.entity.Account;
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
		if(account.getUsername().length()<4)
			return Optional.of(RegistrationError.USERNAME_TOO_SHORT);
		if(account.getPassword().length()<8)
			return Optional.of(RegistrationError.PASSWORD_TOO_SHORT);
		if(!account.getPassword().equals(pswRepeat))
			return Optional.of(RegistrationError.DIFFERENT_PASSWORDS);
		if(!passwordChecker.validatePassword(pswRepeat))
			return Optional.of(RegistrationError.WRONG_PASSWORD);
		if (accountRepository.findByUsername(account.getUsername()) != null)
			return Optional.of(RegistrationError.USERNAME_EXISTS);
		if(accountRepository.findByEmail(account.getEmail())!= null)
			return Optional.of(RegistrationError.EMAIL_EXISTS);
		Role role = getUserRole();
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
		accountRepository.save(account);
		return Optional.empty();
	}

	private Role getUserRole() {
		Optional<Role> optional = roleRepository.findById(RoleEnum.ROLE_USER.toString());
		if (optional.isEmpty()) {
			Role role = new Role(RoleEnum.ROLE_USER.toString());
			roleRepository.save(role);
			return role;
		} else
			return optional.get();
	}

}