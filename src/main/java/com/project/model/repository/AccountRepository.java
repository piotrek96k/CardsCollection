package com.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.entity.Account;
import com.project.model.entity.AccountId;

public interface AccountRepository extends JpaRepository<Account, AccountId> {

	public Account findByUsername(String username);

	public Account findByEmail(String email);
	
}