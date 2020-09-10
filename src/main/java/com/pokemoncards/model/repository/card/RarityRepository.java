package com.pokemoncards.model.repository.card;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pokemoncards.model.entity.Rarity;

public interface RarityRepository extends JpaRepository<Rarity, String>, CardField<Rarity>{

	@Query(value = "select * from rarity order by id asc", nativeQuery = true)
	public List<Rarity> findAllRaritiesOrderById();

	@Transactional
	@Modifying
	@Query(value = "update rarity set cost=:cost where id=:id", nativeQuery = true)
	public void setRarityCost(@Param(value = "id") String id, @Param(value = "cost") int cost);

	@Transactional
	@Modifying
	@Query(value = "update rarity set sell_cost=:sell_cost where id=:id", nativeQuery = true)
	public void setRaritySellCost(@Param(value = "id") String id, @Param(value = "sell_cost") int sellCost);
	
}