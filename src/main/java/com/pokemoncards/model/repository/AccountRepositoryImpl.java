package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;
import com.pokemoncards.model.entity.Set;
import com.pokemoncards.model.entity.Type;
import com.pokemoncards.model.service.SortType;
import com.pokemoncards.model.service.SortType.OrderType;

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
		criteriaQuery.where(getAccountPredicate(criteriaBuilder, account, username));
		Join<Account, Card> card = account.join("cards");
		criteriaQuery.groupBy(card.get("id"));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		criteriaQuery.multiselect(card, criteriaBuilder.count(card));
		return getOrderByQueryPart(criteriaBuilder, criteriaQuery, card, page, sortType, orderType).getResultList();
	}

	@Override
	public int getNumberOfPages(String username, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Account> account = criteriaQuery.from(Account.class);
		Join<Account,Card> card = account.join("cards");
		criteriaQuery.select(criteriaBuilder.countDistinct(card));
		criteriaQuery.where(getAccountPredicate(criteriaBuilder, account, username));
		setWhereQueryPart(criteriaBuilder, criteriaQuery, card, rarities, sets, types, search);
		return getNumberOfPagesFromNumberOfCards(entityManager.createQuery(criteriaQuery).getSingleResult().intValue());
	}

	private Predicate getAccountPredicate(CriteriaBuilder criteriaBuilder, Root<Account> account, String username) {
		return criteriaBuilder.equal(account.get("username"), username);
	}

}