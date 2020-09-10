package com.pokemoncards.config.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.repository.account.AccountRepository;

public class SecurityUserDetails implements UserDetailsService {

	private AccountRepository accountRepository;
	
	private Map<UserDetails,Object> usersLockers;
	
	public SecurityUserDetails(AccountRepository accountRepository, Map<UserDetails, Object> usersLockers) {
		this.accountRepository = accountRepository;
		this.usersLockers = usersLockers;
	}
	
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		Account account = accountRepository.findByUsernameOrEmail(id);
		if (account == null)
			throw new UsernameNotFoundException("User not found");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		account.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getId())));
		ExtendedUser user = new ExtendedUser(account.getUsername(), account.getEmail(), account.getPassword(),
				authorities);
		if(!usersLockers.containsKey(user))
			usersLockers.put(user, Executors.newFixedThreadPool(1));
		return user;
	}

	public Map<UserDetails, Object> getUsersLockers() {
		return Collections.unmodifiableMap(usersLockers);
	}

}
