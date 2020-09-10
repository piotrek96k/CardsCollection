package com.pokemoncards.model.repository.card;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.repository.RepositoryImpl;

public abstract class CardFieldRepositoryImpl <T> extends RepositoryImpl implements CardField<T>{
	
	protected void joinAccountIfUsernameNotEmpty(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery,
			From<?, Card> card, Optional<String> username, Expression<?> value) {
		if (username.isPresent()) {
			Join<Card, Account> account = card.join("accounts", JoinType.LEFT);
			account.on(criteriaBuilder.equal(account.get("username"), username.get()));
			Join<Account, Card> accountCard = account.join("cards", JoinType.LEFT);
			accountCard.on(criteriaBuilder.equal(card.get("id"), accountCard.get("id")));
			criteriaQuery.multiselect(value, criteriaBuilder.countDistinct(card), criteriaBuilder.countDistinct(accountCard));
			criteriaQuery.groupBy(value);
			return;
		}
		criteriaQuery.multiselect(value, criteriaBuilder.countDistinct(card));
		criteriaQuery.groupBy(value);
	}
	
}
