package com.pokemoncards.model.repository.account;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.FreeCard;

public interface FreeCardRepository extends JpaRepository<FreeCard, AccountId> {

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update FreeCard freeCard set freeCard.nextFreeCard=:nextFreeCard where freeCard.username=:username")
	public void updateNextFreeCard(@Param(value = "username") String username,
			@Param(value = "nextFreeCard") LocalDateTime nextFreeCard);
	
	@Query(value = "select freeCard from FreeCard freeCard where freeCard.username=:username")
	public FreeCard findByUsername(@Param(value = "username") String username);

}
