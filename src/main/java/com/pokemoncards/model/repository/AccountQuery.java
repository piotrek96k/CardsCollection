package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import com.pokemoncards.model.component.SortType;
import com.pokemoncards.model.component.SortType.OrderType;
import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;

public interface AccountQuery {

	public AccountId getAccountId(String name);

	public List<Card> getCards(String username, int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
			List<Set> sets, List<Type> types, Optional<String> search);

	public int getNumberOfPages(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

}
