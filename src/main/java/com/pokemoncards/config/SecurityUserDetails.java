package com.pokemoncards.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.repository.AccountRepository;

public class SecurityUserDetails implements UserDetailsService {

	private AccountRepository accountRepository;

	public SecurityUserDetails(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String id)
			throws UsernameNotFoundException {
		Account account = accountRepository.findByUsernameOrEmail(id);
		if (account == null)
			throw new UsernameNotFoundException("User not found");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		account.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getId())));
		return new ExtendedUser(account.getUsername(), account.getEmail(), account.getPassword(), authorities);
	}

}
