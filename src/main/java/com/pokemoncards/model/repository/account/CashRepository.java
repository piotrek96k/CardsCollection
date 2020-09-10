package com.pokemoncards.model.repository.account;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Cash;

public interface CashRepository extends JpaRepository<Cash, AccountId> {

	@Query(value = "select cash from Cash cash where cash.username=:username")
	public Cash getCash(@Param(value = "username") String username);
	
	@Query(value = "select cash.coins from Cash cash where cash.username=:username")
	public int getCoins(@Param(value = "username") String username);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update Cash cash set cash.coins=:coins where cash.username=:username")
	public void updateCoins(@Param(value = "username") String username, @Param(value = "coins") int coins);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update Cash cash set cash.coins=:coins, cash.nextCoinsCollecting=:nextCoinsCollecting, cash.daysInRow=:daysInRow where cash.username=:username")
	public void updateCoinsCollect(@Param(value = "username") String username, @Param(value = "coins") int coins,
			@Param(value = "nextCoinsCollecting") LocalDateTime dateTime, @Param(value = "daysInRow") int daysInRow);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update Cash cash set cash.daysInRow=:daysInRow where cash.username=:username")
	public void updateDaysInRow(@Param(value = "username") String username, @Param(value = "daysInRow") int daysInRow);

}