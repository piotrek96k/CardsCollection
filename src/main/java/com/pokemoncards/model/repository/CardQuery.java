package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.service.SortType;
import com.pokemoncards.model.service.SortType.OrderType;

public interface CardQuery {

	public int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search);

	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			List<Type> types, Optional<String> search);
	
	public Card getCardByRowNumber(int row, Rarity rarity);

	public Card getFirstCard(Rarity rarity);
	
	public Card getNextCard(Rarity rarity, String id);
	
	public Card getFirstCard(String search);
	
	public Card getNextCard(String search, String id);
	
	public Card getCardByRowNumber(int row, String search);
	
	public int countCardsBySearch(String search);
	
}
