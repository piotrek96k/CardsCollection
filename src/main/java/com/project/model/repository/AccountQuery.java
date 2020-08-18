package com.project.model.repository;

import java.util.List;
import java.util.Optional;

import com.project.model.entity.AccountId;
import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.entity.Type;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public interface AccountQuery {

	public AccountId getAccountId(String name);

	public List<Card> getCards(String username, int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
			List<Set> sets, List<Type> types, Optional<String> search);

	public int getNumberOfPages(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search);

}
