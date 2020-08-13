package com.project.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public class CardRepositoryImpl implements CardQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities,sets, search);
		return getOrderByQueryPart(criteriaBuilder, criteriaQuery, card, page, sortType, orderType).getResultList();
	}

	@Override
	public int getNumberOfPages(List<Rarity> rarities, List<Set> sets, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.select(criteriaBuilder.count(card));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, search);
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(criteriaQuery).getSingleResult());
	}

	private <T> void setWhereQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<Card> card,
			List<Rarity> rarities, List<Set> sets, Optional<String> search) {
		Predicate predicate = null;
		if (!rarities.isEmpty())
			predicate = getRaritiesPredicate(criteriaBuilder, card, rarities);
		if (!sets.isEmpty())
			if (predicate == null)
				predicate = getSetsPredicate(criteriaBuilder, card, sets);
			else
				predicate = criteriaBuilder.and(predicate, getSetsPredicate(criteriaBuilder, card, sets));
		if (search.isPresent())
			if (predicate == null)
				predicate = getSearchPredicate(criteriaBuilder, card, search.get());
			else
				predicate = criteriaBuilder.and(predicate, getSearchPredicate(criteriaBuilder, card, search.get()));
		if (predicate != null)
			criteriaQuery.where(predicate);
	}

	private Predicate getRaritiesPredicate(CriteriaBuilder criteriaBuilder, Root<Card> card, List<Rarity> rarities) {
		return criteriaBuilder.in(card.get("rarity")).value(rarities);
	}

	private Predicate getSetsPredicate(CriteriaBuilder criteriaBuilder, Root<Card> card, List<Set> sets) {
		return criteriaBuilder.in(card.get("set")).value(sets);
	}

	private Predicate getSearchPredicate(CriteriaBuilder criteriaBuilder, Root<Card> card, String search) {
		return criteriaBuilder.like(criteriaBuilder.upper(card.get("name")), ("%" + search + "%").toUpperCase());

	}

	private TypedQuery<Card> getOrderByQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			Root<Card> card, int page, SortType sortType, OrderType orderType) {
		Function<Expression<?>, Order> method = getOrderTypeMethod(criteriaBuilder, card, sortType, orderType);
		Order[] orders = { getRequestedOrder(sortType, card, method), method.apply(card.get("name")),
				method.apply(card.get("id")) };
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery.orderBy(orders));
		setPaginationQueryPart(typedQuery, page);
		return typedQuery;
	}

	private Function<Expression<?>, Order> getOrderTypeMethod(CriteriaBuilder criteriaBuilder, Root<Card> card,
			SortType sortType, OrderType orderType) {
		if (orderType.equals(sortType.ASC))
			return expression -> criteriaBuilder.asc(expression);
		return expression -> criteriaBuilder.desc(expression);
	}

	private Order getRequestedOrder(SortType sortType, Root<Card> card, Function<Expression<?>, Order> method) {
		if (sortType.equals(SortType.RARITY) || sortType.equals(SortType.COST)) {
			Join<Card, Rarity> rarity = card.join("rarity");
			return method.apply(rarity.get(sortType.getColumnName()));
		}
		return method.apply(card.get(sortType.getColumnName()));
	}

	private <T> void setPaginationQueryPart(TypedQuery<T> query, int page) {
		query.setFirstResult((page - 1) * PAGE_SIZE);
		query.setMaxResults(PAGE_SIZE);
	}

}