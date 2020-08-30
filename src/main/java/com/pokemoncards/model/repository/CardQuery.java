package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import com.pokemoncards.model.component.SortType;
import com.pokemoncards.model.component.SortType.OrderType;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;

public interface CardQuery {

	public int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search);

	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			List<Type> types, Optional<String> search, Optional<String> username);
	
	public Card getCardByRowNumber(int row, Rarity rarity, Optional<String> username);

	public Card getFirstCard(Rarity rarity, Optional<String> username);
	
	public Card getNextCard(Rarity rarity, String id, Optional<String> username);
	
	public Card getFirstCard(String search, Optional<String> username);
	
	public Card getNextCard(String search, String id, Optional<String> username);
	
	public Card getCardByRowNumber(int row, String search, Optional<String> username);
	
	public int countCardsBySearch(String search);
	
	public Optional<Card> findById(String id, Optional<String> username);
		
}
