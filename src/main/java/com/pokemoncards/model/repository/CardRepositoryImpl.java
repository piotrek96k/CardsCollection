package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.service.SortType;
import com.pokemoncards.model.service.SortType.OrderType;

@Repository
public class CardRepositoryImpl extends RepositoryImpl implements CardQuery {

	@Override
	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			List<Type> types, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		return getOrderByQueryPart(criteriaBuilder, criteriaQuery, card, page, sortType, orderType).getResultList();
	}

	@Override
	public int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(card));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(criteriaQuery).getSingleResult());
	}

}