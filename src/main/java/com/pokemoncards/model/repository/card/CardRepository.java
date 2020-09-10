package com.pokemoncards.model.repository.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pokemoncards.model.entity.Card;

public interface CardRepository extends JpaRepository<Card, String>, CardQuery {

	@Query(value = "select count(rarity_id) from card where rarity_id=:rarity_id", nativeQuery = true)
	public int countCardsByRarity(@Param(value = "rarity_id") String rarityId);

	@Query(value = "select rarity.cost from card inner join rarity on card.rarity_id=rarity.id where card.id=:id", nativeQuery = true)
	public int getCardCost(@Param(value = "id") String id);

	@Query(value = "select rarity.sell_price from card inner join rarity on card.rarity_id=rarity.id where card.id=:id", nativeQuery = true)
	public int getCardSellCost(@Param(value = "id") String id);
	
	@Query(value = "select * from card order by random() limit(1)", nativeQuery = true)
	public Card getRandomCard();

}
