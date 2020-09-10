package com.pokemoncards.model.repository.account;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.pokemoncards.exception.NotFoundException;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.repository.RepositoryImpl;
import com.pokemoncards.model.session.SortType;
import com.pokemoncards.model.session.SortType.OrderType;

@Repository
public class AccountRepositoryImpl extends RepositoryImpl implements AccountQuery {

	@Override
	public AccountId getAccountId(String name) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AccountId> query = criteriaBuilder.createQuery(AccountId.class);
		Root<Account> account = query.from(Account.class);
		query.where(criteriaBuilder.or(criteriaBuilder.equal(account.get("username"), name),
				criteriaBuilder.equal(account.get("email"), name)));
		query.multiselect(account.get("username"), account.get("email"));
		List<AccountId> list = entityManager.createQuery(query).getResultList();
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	public List<Card> getCards(String username, int page, SortType sortType, OrderType orderType, List<Rarity> rarities,
			List<Set> sets, List<Type> types, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
		Root<Account> account = criteriaQuery.from(Account.class);
		Join<Account, Card> card = account.join("cards");
		criteriaQuery.groupBy(card.get("id"));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, account, card, rarities, sets, types, search, username);
		criteriaQuery.multiselect(card, criteriaBuilder.count(card));
		return getOrderByQueryPart(criteriaBuilder, criteriaQuery, card, page, sortType, orderType).getResultList();
	}

	@Override
	public int getNumberOfCards(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Account> account = criteriaQuery.from(Account.class);
		Join<Account, Card> card = account.join("cards");
		criteriaQuery.select(criteriaBuilder.countDistinct(card));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, account, card, rarities, sets, types, search, username);
		return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
	}

	protected <T> void setWhereQueryPart(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery,
			From<?, Account> account, From<?, Card> card, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search, String username) {
		Predicate predicate = getWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		Predicate userPredicate = getAccountPredicate(criteriaBuilder, account, username);
		if (predicate == null)
			criteriaQuery.where(userPredicate);
		else
			criteriaQuery.where(criteriaBuilder.and(userPredicate, predicate));
	}

	@Override
	public int getCardsValues(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<Account> account = criteriaQuery.from(Account.class);
		Join<Account, Card> card = account.join("cards");
		setWhereQueryPart(criteriaBuilder, criteriaQuery, account, card, rarities, sets, types, search, username);
		criteriaQuery.select(criteriaBuilder.sum(card.get("rarity").get("value")));
		List<Integer> result = entityManager.createQuery(criteriaQuery).getResultList();
		return result.get(0) == null ? 0 : result.get(0);
	}

	@Transactional
	@Modifying
	@Override
	public void addCards(String username, String email, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		List<String> cardsIds = getCardsIds(rarities, sets, types, search);
		if (cardsIds.size() == 0)
			throw new NotFoundException();
		StringBuilder builder = new StringBuilder();
		builder.append("insert into account_cards(username, email, card_id) values");
		cardsIds.forEach(card -> builder.append("('").append(username).append("','").append(email).append("','")
				.append(card).append("'),"));
		builder.deleteCharAt(builder.length() - 1);
		entityManager.createNativeQuery(builder.toString()).executeUpdate();
	}

	@Transactional
	@Modifying
	@Override
	public void removeCards(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		List<String> cardsIds = getCardsIds(rarities, sets, types, search);
		if (cardsIds.size() == 0)
			throw new NotFoundException();
		StringBuilder builder = new StringBuilder();
		builder.append("delete from account_cards where username='").append(username)
				.append("' and account_cards.card_id in (");
		cardsIds.forEach(id -> builder.append("'").append(id).append("',"));
		builder.deleteCharAt(builder.length() - 1).append(")");
		entityManager.createNativeQuery(builder.toString()).executeUpdate();
	}

	private List<String> getCardsIds(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		criteriaQuery.select(card.get("id"));
		criteriaQuery.groupBy(card.get("id"));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		List<String> result = entityManager.createQuery(criteriaQuery).getResultList();
		return result;
	}

}