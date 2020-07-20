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
	AccountRepository accountRepository;

	@Autowired
	RoleRepository roleRepository;

	public Optional<RegistrationError> addAccount(Account account, String pswRepeat) {
		if (accountRepository.findByUsername(account.getUsername()) != null)
			return Optional.of(RegistrationError.USERNAME_EXISTS);
		if(accountRepository.findByEmail(account.getEmail())!= null)
			return Optional.of(RegistrationError.EMAIL_EXISTS);
		Optional<Role> optional = roleRepository.findById(RoleEnum.ROLE_USER.toString());
		Role role;
		if (optional.isEmpty()) {
			role = new Role(RoleEnum.ROLE_USER.toString());
			roleRepository.save(role);
		} else
			role = optional.get();
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
		accountRepository.save(account);
		return Optional.empty();
	}

}