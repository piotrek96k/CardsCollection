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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.entity.Type;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public class CardRepositoryImpl implements CardQuery {

	@PersistenceContext
	private EntityManager entityManager;

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

	private <T> void setWhereQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<Card> card,
			List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		Predicate predicate = null;
		if (!rarities.isEmpty())
			predicate = getRaritiesPredicate(criteriaBuilder, card, rarities);
		if (!sets.isEmpty())
			if (predicate == null)
				predicate = getSetsPredicate(criteriaBuilder, card, sets);
			else
				predicate = criteriaBuilder.and(predicate, getSetsPredicate(criteriaBuilder, card, sets));
		if (!types.isEmpty()) {
			if (predicate == null)
				predicate = getTypesPredicate(criteriaBuilder, card, types);
			else
				predicate = criteriaBuilder.and(predicate, getTypesPredicate(criteriaBuilder, card, types));
		}
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

	private Predicate getTypesPredicate(CriteriaBuilder criteriaBuilder, Root<Card> card, List<Type> types) {
		Predicate predicate = criteriaBuilder.disjunction();
		Join<Card, Type> join = card.join("types");
		for (Type type : types)
			predicate = criteriaBuilder.or(criteriaBuilder.equal(join, type), predicate);
		return predicate;
	}

	private Predicate getSearchPredicate(CriteriaBuilder criteriaBuilder, Root<Card> card, String search) {
		return criteriaBuilder.like(criteriaBuilder.upper(card.get("name")), ("%" + search + "%").toUpperCase());
	}

	private TypedQuery<Card> getOrderByQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			Root<Card> card, int page, SortType sortType, OrderType orderType) {
		Function<Expression<?>, Order> method = getOrderTypeMethod(criteriaBuilder, card, sortType, orderType);
		Order[] orders = { getRequestedOrder(criteriaQuery, card, method, sortType), method.apply(card.get("name")),
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

	private Order getRequestedOrder(CriteriaQuery<Card> criteriaQuery, Root<Card> card,
			Function<Expression<?>, Order> method, SortType sortType) {
		if (sortType.equals(SortType.NAME) || sortType.equals(SortType.POKEDEX))
			return method.apply(card.get(sortType.getColumnName()));
		if (sortType.equals(SortType.RARITY) || sortType.equals(SortType.COST))
			return joinToOrder( card, method,sortType,"rarity");
		if (sortType.equals(SortType.SET))
			return joinToOrder( card, method, sortType,"set");
		return joinToOrder(card, method, sortType,"firstType");
	}

	private <T> Order joinToOrder(Root<Card> card, Function<Expression<?>, Order> method, SortType sortType,
			String attribute) {
		Join<Card, T> join = card.join(attribute, JoinType.LEFT);
		return method.apply(join.get(sortType.getColumnName()));
	}

	private <T> void setPaginationQueryPart(TypedQuery<T> query, int page) {
		query.setFirstResult((page - 1) * PAGE_SIZE);
		query.setMaxResults(PAGE_SIZE);
	}

}