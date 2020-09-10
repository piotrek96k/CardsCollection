package com.pokemoncards.model.repository.card;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;

public class RarityRepositoryImpl extends CardFieldRepositoryImpl<Rarity>{

	@Override
	public List<Rarity> findAll(Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rarity> criteriaQuery = criteriaBuilder.createQuery(Rarity.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username, card.get("rarity"));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	public Optional<Rarity> findById(String id, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rarity> criteriaQuery = criteriaBuilder.createQuery(Rarity.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username, card.get("rarity"));
		criteriaQuery.where(criteriaBuilder.equal(card.get("rarity").get("id"), id));
		TypedQuery<Rarity> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Rarity> result = typedQuery.getResultList();
		return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
	}

}