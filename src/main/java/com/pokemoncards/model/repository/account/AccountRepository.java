package com.pokemoncards.model.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.AccountId;

public interface AccountRepository extends JpaRepository<Account, AccountId>, AccountQuery {

	public Account findByUsername(String username);

	public Account findByEmail(String email);
	
	@Query(value = "select account from Account account where username=:id or email=:id")
	public Account findByUsernameOrEmail(String id);

	@Transactional
	@Modifying
	@Query(value = "insert into account_cards(username, email, card_id) values (:username, :email, :card_id)", nativeQuery = true)
	public void addCard(@Param("username") String username, @Param("email") String email,
			@Param("card_id") String cardId);

	@Transactional
	@Modifying
	@Query(value = "delete from account_cards where ctid =(select ctid from account_cards where username=:username and card_id=:card_id limit(1))", nativeQuery = true)
	public void removeCard(@Param("username") String username, @Param("card_id") String cardId);

	@Transactional
	@Modifying
	@Query(value = "insert into account_roles(username, email, role_id) values (:username, :email, :role_id)", nativeQuery = true)
	public void addRole(@Param("username") String username, @Param("email") String email, @Param("role_id") String roleId);
	
	@Query(value = "select count(card_id) from account_cards where username=:username and card_id = :card_id", nativeQuery = true)
	public int countUserCardsByCardId(@Param("username") String username, @Param("card_id") String cardId);
	
}