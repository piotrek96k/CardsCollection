package com.project.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.model.entity.Card;

public interface CardRepository extends JpaRepository<Card, String>, CardQuery{
	
	@Query(value = "select * from card order by name, id asc limit(100) offset(((:page)-1)*100)" , nativeQuery = true)
	public List<Card> getCardsByPageOrderByName(@Param("page")int page);
	
	@Query(value = "select count(rarity_id) from card where rarity_id=:rarity_id", nativeQuery = true)
	public int countCardsByRarity(@Param("rarity_id") String rarityId);
	
	@Query(value = "select rarity.cost from card inner join rarity on card.rarity_id=rarity.id where card.id=:id", nativeQuery = true)
	public int getCardCost(@Param("id")String id);
		
}
