package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
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

	@Override
	public Card getCardByRowNumber(int row, Rarity rarity) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.where(getRarityPredicate(criteriaBuilder, card, rarity));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(row);
		typedQuery.setMaxResults(1);
		return typedQuery.getResultStream().count() > 0 ? typedQuery.getSingleResult() : getFirstCard(rarity);
	}

	@Override
	public Card getFirstCard(Rarity rarity) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.where(getRarityPredicate(criteriaBuilder, card, rarity));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		return typedQuery.getSingleResult();
	}

	@Override
	public Card getNextCard(Rarity rarity, String id) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.greaterThan(card.get("id"), id),
				getRarityPredicate(criteriaBuilder, card, rarity)));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		return typedQuery.getResultStream().count() > 0 ? typedQuery.getSingleResult() : getFirstCard(rarity);
	}

	@Override
	public Card getFirstCard(String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.where(getSearchPredicate(criteriaBuilder, card, search));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		return typedQuery.getResultStream().count() > 0 ? typedQuery.getSingleResult() : null;
	}

	@Override
	public Card getNextCard(String search, String id) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.greaterThan(card.get("id"), id),
				getSearchPredicate(criteriaBuilder, card, search)));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		return typedQuery.getResultStream().count() > 0 ? typedQuery.getSingleResult() : getFirstCard(search);
	}
	
	@Override
	public Card getCardByRowNumber(int row, String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.where(getSearchPredicate(criteriaBuilder, card, search));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(row);
		typedQuery.setMaxResults(1);
		return typedQuery.getResultStream().count() > 0 ? typedQuery.getSingleResult() : getFirstCard(search);
	}

	@Override
	public int countCardsBySearch(String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(card));
		criteriaQuery.where(getSearchPredicate(criteriaBuilder, card, search));
		return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
	}

	private Predicate getRarityPredicate(CriteriaBuilder criteriaBuilder, From<?, Card> card, Rarity rarity) {
		return criteriaBuilder.equal(card.get("rarity").get("id"), rarity.getId());
	}

	private void setOrderByIdQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			From<?, Card> card) {
		criteriaQuery.orderBy(criteriaBuilder.asc(card.get("id")));

	}

}