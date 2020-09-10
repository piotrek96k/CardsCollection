package com.pokemoncards.model.repository.account;

import java.util.List;
import java.util.Optional;

import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.session.SortType;
import com.pokemoncards.model.session.SortType.OrderType;

public interface AccountQuery {

	public AccountId getAccountId(String name);

	public List<Card> getCards(String username, int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
			List<Set> sets, List<Type> types, Optional<String> search);

	public int getNumberOfCards(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

	public int getCardsValues(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

	public void addCards(String username, String email, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

	public void removeCards(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

}
