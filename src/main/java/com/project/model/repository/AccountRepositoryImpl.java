package com.project.model.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import com.project.model.entity.Account;
import com.project.model.entity.AccountId;
import com.project.model.entity.Card;

@Service
public class AccountRepositoryImpl implements AccountQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public int countDistinctCardsByUsername(String username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Account> account = query.from(Account.class);
		query.select(criteriaBuilder.countDistinct(account.join("cards")));
		return entityManager.createQuery(query).getSingleResult().intValue();
	}

	@Override
	public List<Card> getAccountCardsListByPage(String username, int page) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Card> query = criteriaBuilder.createQuery(Card.class);
		Root<Account> account = query.from(Account.class);
		query.where(criteriaBuilder.equal(account.get("username"), username));
		Join<Account, Card> card = account.join("cards");
		query.groupBy(card.get("id"));
		query.multiselect(card, criteriaBuilder.count(card));
		query.orderBy(criteriaBuilder.asc(card.get("name")), criteriaBuilder.asc(card.get("id")));
		TypedQuery<Card> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((page - 1) * PAGE_SIZE);
		typedQuery.setMaxResults(PAGE_SIZE);
		return typedQuery.getResultList();
	}

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
	public int getNumberOfPages(String username) {
		int cards = countDistinctCardsByUsername(username);
		return getNumberOfPagesFromNumberOfCards(cards);
	}

}