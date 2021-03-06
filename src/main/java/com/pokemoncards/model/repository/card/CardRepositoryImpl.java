package com.pokemoncards.model.repository.card;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.RepositoryImpl;
import com.pokemoncards.model.session.SortType;
import com.pokemoncards.model.session.SortType.OrderType;

@Repository
public class CardRepositoryImpl extends RepositoryImpl implements CardQuery {

	@Override
	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			List<Type> types, Optional<String> search, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.groupBy(card.get("id"));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		return getOrderByQueryPart(criteriaBuilder, criteriaQuery, card, page, sortType, orderType).getResultList();
	}

	@Override
	public int getNumberOfCards(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(card));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
	}

	@Override
	public Card getCardByRowNumber(int row, Rarity rarity, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(getRarityPredicate(criteriaBuilder, card, rarity));
		criteriaQuery.groupBy(card.get("id"));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(row);
		typedQuery.setMaxResults(1);
		List<Card> result = typedQuery.getResultList();
		return result.size() > 0 ? result.get(0) : getFirstCard(rarity, username);
	}

	@Override
	public Card getFirstCard(Rarity rarity, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(getRarityPredicate(criteriaBuilder, card, rarity));
		criteriaQuery.groupBy(card.get("id"));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		return typedQuery.getSingleResult();
	}

	@Override
	public Card getNextCard(Rarity rarity, String id, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.greaterThan(card.get("id"), id),
				getRarityPredicate(criteriaBuilder, card, rarity)));
		criteriaQuery.groupBy(card.get("id"));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		List<Card> result = typedQuery.getResultList();
		return result.size() > 0 ? result.get(0) : getFirstCard(rarity, username);
	}

	@Override
	public Card getFirstCard(String search, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(getSearchPredicate(criteriaBuilder, card, search));
		criteriaQuery.groupBy(card.get("id"));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		List<Card> result = typedQuery.getResultList();
		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public Card getNextCard(String search, String id, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.greaterThan(card.get("id"), id),
				getSearchPredicate(criteriaBuilder, card, search)));
		criteriaQuery.groupBy(card.get("id"));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);
		List<Card> result = typedQuery.getResultList();
		return result.size() > 0 ? result.get(0) : getFirstCard(search, username);
	}

	@Override
	public Card getCardByRowNumber(int row, String search, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(getSearchPredicate(criteriaBuilder, card, search));
		criteriaQuery.groupBy(card.get("id"));
		setOrderByIdQueryPart(criteriaBuilder, criteriaQuery, card);
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(row);
		typedQuery.setMaxResults(1);
		List<Card> result = typedQuery.getResultList();
		return result.size() > 0 ? result.get(0) : getFirstCard(search, username);
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

	private void joinAccountIfUsernameNotEmpty(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			From<?, Card> card, Optional<String> username) {
		if (username.isPresent()) {
			Join<Card, Account> account = card.join("accounts", JoinType.LEFT);
			account.on(criteriaBuilder.equal(account.get("username"), username.get()));
			criteriaQuery.multiselect(card, criteriaBuilder.count(account));
		}
	}

	private Predicate getRarityPredicate(CriteriaBuilder criteriaBuilder, From<?, Card> card, Rarity rarity) {
		return criteriaBuilder.equal(card.get("rarity").get("id"), rarity.getId());
	}

	private void setOrderByIdQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<Card> criteriaQuery,
			From<?, Card> card) {
		criteriaQuery.orderBy(criteriaBuilder.asc(card.get("id")));
	}

	@Override
	public Optional<Card> findById(String id, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username);
		criteriaQuery.where(criteriaBuilder.equal(card.get("id"), id));
		criteriaQuery.groupBy(card.get("id"));
		TypedQuery<Card> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Card> result = typedQuery.getResultList();
		return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
	}

	@Override
	public int getCardsValue(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.select(criteriaBuilder.sum(card.get("rarity").get("value")));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		TypedQuery<Integer> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Integer> result = typedQuery.getResultList();
		return result.get(0) != null ? result.get(0) : 0;
	}

//	@Override
//	public List<String> getCardsIds(int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
//			List<Set> sets, List<Type> types, Optional<String> search) {
//		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
//		Root<Card> card = criteriaQuery.from(Card.class);
//		criteriaQuery.select(card.get("id"));
//		criteriaQuery.groupBy(card.get("id"));
//		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
//		return entityManager.createQuery(criteriaQuery).getResultList();
//	}

}