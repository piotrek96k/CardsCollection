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
	public void addCard(@Param("username") String username, @Param("email") String email,
			@Param("card_id") String cardId);

	@Transactional
	@Modifying
	@Query(value = "delete from account_cards where ctid IN (SELECT ctid FROM account_cards where username=:username and email=:email and card_id=:card_id\n" +
			"LIMIT 1\n" +
			")", nativeQuery = true)
	public void removeCard(@Param("username") String username, @Param("email") String email,
						@Param("card_id") String cardId);

	@Query(value = "select count(card_id) from account_cards where username=:username and card_id = :card_id", nativeQuery = true)
	public int countUserCardsByCardId(@Param("username") String username, @Param("card_id") String cardId);

	@Query(value = "select coins from account where username=:username", nativeQuery = true)
	public int getCoins(@Param("username") String username);

	@Transactional
	@Modifying
	@Query(value = "update account set coins=:coins where username=:username", nativeQuery = true)
	public void updateUserCoins(@Param("username") String username, @Param("coins") int coins);

}