package com.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.entity.Account;
import com.project.model.entity.AccountId;

public interface AccountRepository extends JpaRepository<Account, AccountId>, AccountQuery {

	public Account findByUsername(String username);

	public Account findByEmail(String email);
	
	@Transactional
    @Modifying
	@Query(value = "insert into account_cards(username, email, card_id) values (:username, :email, :card_id)", nativeQuery = true)
	public void addCard(@Param("username")String username, @Param("email") String email, @Param("card_id") String cardId);
	
}