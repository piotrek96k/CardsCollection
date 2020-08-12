package com.project.model.repository;

import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public class CardRepositoryImpl implements CardQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Card> getCardsByPageOrderByValue(int page, SortType sortType, OrderType orderType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> query = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = query.from(Card.class);
		return getOrderByQueryPart(page, sortType, orderType, query, card).getResultList();
	}

	@Override
	public List<Card> getCardsByPageOrderByValueWithSelectedRarities(int page, SortType sortType, OrderType orderType,
			List<Rarity> rarities) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> query = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = query.from(Card.class);
		query.where(criteriaBuilder.in(card.get("rarity")).value(rarities));
		return getOrderByQueryPart(page, sortType, orderType, query, card).getResultList();
	}
	
	@Override
	public List<Card> getCardsByPageOrderByValueWithSearch(int page, SortType sortType, OrderType orderType,
			String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> query = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = query.from(Card.class);
		query.where(criteriaBuilder.like(criteriaBuilder.upper(card.get("name")), ("%"+search+"%").toUpperCase()));
		return getOrderByQueryPart(page, sortType, orderType, query, card).getResultList();
	}
	
	@Override
	public List<Card> getCardsByPageOrderByValueWithSelectedRaritiesWithSearch(int page, SortType sortType,
			OrderType orderType, List<Rarity> rarities, String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> query = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = query.from(Card.class);
		Predicate raritiesPredicate = getSelectedRArietiesPredicate(rarities, card);
		Predicate searchPredicate =getSearchPredicate(search, card);
		query.where(criteriaBuilder.and(raritiesPredicate, searchPredicate));
		return getOrderByQueryPart(page, sortType, orderType, query, card).getResultList();
	}
	
	private Predicate getSelectedRArietiesPredicate(List<Rarity> rarities, Root<Card> card) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		return criteriaBuilder.in(card.get("rarity")).value(rarities);
	}
	
	private Predicate getSearchPredicate(String search, Root<Card> card) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		return criteriaBuilder.like(criteriaBuilder.upper(card.get("name")), ("%"+search+"%").toUpperCase());
	}

	private TypedQuery<Card> getOrderByQueryPart(int page, SortType sortType, OrderType orderType,
			CriteriaQuery<Card> query, Root<Card> card) {
		Function<String, Order> method = getOrderTypeMethod(sortType, orderType, card);
		Order[] orders = { method.apply(sortType.getColumnName()), method.apply("name"), method.apply("id") };
		TypedQuery<Card> typedQuery = entityManager.createQuery(query.orderBy(orders));
		setPaginationQueryPart(page, typedQuery);
		return typedQuery;
	}
	
	private <T> void setPaginationQueryPart(int page,TypedQuery<T> query){
		query.setFirstResult((page - 1) * PAGE_SIZE);
		query.setMaxResults(PAGE_SIZE);
	}

	private Function<String, Order> getOrderTypeMethod(SortType sortType, OrderType orderType, Root<Card> card) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		if (orderType.equals(sortType.ASC))
			return string -> criteriaBuilder.asc(card.get(string));
		return string -> criteriaBuilder.desc(card.get(string));
	}
	
	@Override
	public int getNumberOfPages() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = query.from(Card.class);
		query.select(criteriaBuilder.count(card));
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(query).getSingleResult());
	}

	@Override
	public int getNumberOfPagesWithSelectedRarities(List<Rarity> rarities) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = query.from(Card.class);
		query.select(criteriaBuilder.count(card));
		query.where(getSelectedRArietiesPredicate(rarities, card));
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(query).getSingleResult());
	}

	@Override
	public int getNumberOfPagesWithSearch(String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = query.from(Card.class);
		query.select(criteriaBuilder.count(card));
		query.where(getSearchPredicate(search, card));
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(query).getSingleResult());
	}

	@Override
	public int getNumberOfPagesWithSelectedRaritiesWithSearch(List<Rarity> rarities, String search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = query.from(Card.class);
		query.select(criteriaBuilder.count(card));
		Predicate raritiesPredicate = getSelectedRArietiesPredicate(rarities, card);
		Predicate searchPredicate =getSearchPredicate(search, card);
		query.where(criteriaBuilder.and(raritiesPredicate, searchPredicate));
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(query).getSingleResult());
	}

}