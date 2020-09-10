package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.session.SortType;
import com.pokemoncards.model.session.SortType.OrderType;

@Repository
public abstract class RepositoryImpl implements Paginable{

	@PersistenceContext
	protected EntityManager entityManager;

	protected <T> void setWhereQueryPart(CriteriaBuilder criteriaBuilder, AbstractQuery<T> criteriaQuery,
			From<?, Card> card, List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		Predicate predicate = getWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		if (predicate != null)
			criteriaQuery.where(predicate);
	}

	protected <T> Predicate getWhereQueryPart(CriteriaBuilder criteriaBuilder, AbstractQuery<T> criteriaQuery,
			From<?, Card> card, List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		Predicate predicate = null;
		Function<List<Rarity>, Predicate> raritiesMethod = rarity -> getRaritiesPredicate(criteriaBuilder, card,
				rarity);
		predicate = getWhereQueryPartPredicate(criteriaBuilder, predicate, List::isEmpty, raritiesMethod, rarities);
		Function<List<Set>, Predicate> setsMethod = set -> getSetsPredicate(criteriaBuilder, card, set);
		predicate = getWhereQueryPartPredicate(criteriaBuilder, predicate, List::isEmpty, setsMethod, sets);
		Function<List<Type>, Predicate> typesPredicate = type -> getTypesPredicate(criteriaBuilder, card, type);
		predicate = getWhereQueryPartPredicate(criteriaBuilder, predicate, List::isEmpty, typesPredicate, types);
		Function<Optional<String>, Predicate> searchMethod = s -> getSearchPredicate(criteriaBuilder, card, s.get());
		predicate = getWhereQueryPartPredicate(criteriaBuilder, predicate, Optional::isEmpty, searchMethod, search);
		return predicate;
	}

	private <T> Predicate getWhereQueryPartPredicate(CriteriaBuilder criteriaBuilder, Predicate predicate,
			Function<T, Boolean> booleanExtractor, Function<T, Predicate> method, T object) {
		if (!booleanExtractor.apply(object)) {
			if (predicate == null)
				return method.apply(object);
			return criteriaBuilder.and(predicate, method.apply(object));
		}
		return predicate;
	}

	protected Predicate getRaritiesPredicate(CriteriaBuilder criteriaBuilder, From<?, Card> card,
			List<Rarity> rarities) {
		return criteriaBuilder.in(card.get("rarity")).value(rarities);
	}

	protected Predicate getSetsPredicate(CriteriaBuilder criteriaBuilder, From<?, Card> card, List<Set> sets) {
		return criteriaBuilder.in(card.get("set")).value(sets);
	}

	protected Predicate getTypesPredicate(CriteriaBuilder criteriaBuilder, From<?, Card> card, List<Type> types) {
		Predicate predicate = criteriaBuilder.disjunction();
		Join<Card, Type> join = card.join("types");
		for (Type type : types)
			predicate = criteriaBuilder.or(criteriaBuilder.equal(join, type), predicate);
		return predicate;
	}

	protected Predicate getSearchPredicate(CriteriaBuilder criteriaBuilder, From<?, Card> card, String search) {
		search = processSearchString(search);
		Predicate predicate = criteriaBuilder.like(criteriaBuilder.upper(card.get("name")), search);
		predicate = criteriaBuilder.or(predicate,
				criteriaBuilder.like(criteriaBuilder.upper(card.get("rarity").get("id")), search));
		predicate = criteriaBuilder.or(predicate,
				criteriaBuilder.like(criteriaBuilder.upper(card.get("set").get("id")), search));
		predicate = criteriaBuilder.or(predicate,
				criteriaBuilder.like(criteriaBuilder.upper(card.get("evolvesFrom")), search));
		predicate = criteriaBuilder.or(predicate,
				criteriaBuilder.like(card.get("pokedexNumber").as(String.class), search));
		predicate = criteriaBuilder.or(predicate,
				criteriaBuilder.like(criteriaBuilder.upper(card.join("types", JoinType.LEFT).get("id")), search));
		return predicate;
	}

	private String processSearchString(String search) {
		search = search.trim().replace("%", "\\%");
		search = search.replace("_", "\\_");
		if (search.startsWith("!"))
			return search.substring(1).trim().toUpperCase();
		return ('%' + search + '%').toUpperCase();
	}

	protected Predicate getAccountPredicate(CriteriaBuilder criteriaBuilder, From<?, Account> account,
			String username) {
		return criteriaBuilder.equal(account.get("username"), username);
	}

	protected TypedQuery<Card> getOrderByQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			From<?, Card> card, int page, SortType sortType, OrderType orderType) {
		Function<Expression<?>, Order> method = getOrderTypeMethod(criteriaBuilder, card, sortType, orderType);
		Order[] orders = { getRequestedOrder(criteriaBuilder, criteriaQuery, card, method, sortType),
				method.apply(card.get("name")), method.apply(card.get("id")) };
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery.orderBy(orders));
		setPaginationQueryPart(typedQuery, page);
		return typedQuery;
	}

	private Function<Expression<?>, Order> getOrderTypeMethod(CriteriaBuilder criteriaBuilder, From<?, Card> card,
			SortType sortType, OrderType orderType) {
		if (orderType.equals(sortType.ASC))
			return expression -> criteriaBuilder.asc(expression);
		return expression -> criteriaBuilder.desc(expression);
	}

	private Order getRequestedOrder(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			From<?, Card> card, Function<Expression<?>, Order> method, SortType sortType) {
		if (sortType.equals(SortType.NAME) || sortType.equals(SortType.POKEDEX))
			return method.apply(card.get(sortType.getColumnName()));
		if (sortType.equals(SortType.RARITY))
			return joinToOrder(criteriaQuery, card, method, sortType, "rarity", "id");
		if (sortType.equals(SortType.VALUE))
			return joinToOrder(criteriaQuery, card, method, sortType, "rarity", "value");
		if (sortType.equals(SortType.SET))
			return joinToOrder(criteriaQuery, card, method, sortType, "set", "id");
		return joinToOrder(criteriaQuery, card, method, sortType, "firstType", "id");
	}

	private <T> Order joinToOrder(CriteriaQuery<Card> criteriaQuery, From<?, Card> card,
			Function<Expression<?>, Order> method, SortType sortType, String attribute, String group) {
		Join<Card, T> join = card.join(attribute, JoinType.LEFT);
		criteriaQuery.groupBy(card.get("id"), join.get(group));
		return method.apply(join.get(sortType.getColumnName()));
	}

	private <T> void setPaginationQueryPart(TypedQuery<T> query, int page) {
		query.setFirstResult((page - 1) * PAGE_SIZE);
		query.setMaxResults(PAGE_SIZE);
	}

}