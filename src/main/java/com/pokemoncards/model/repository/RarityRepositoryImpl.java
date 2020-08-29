package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Rarity;

public class RarityRepositoryImpl extends CardFieldRepositoryImpl<Rarity> {

	@Override
	public List<Rarity> findAll(Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rarity> criteriaQuery = criteriaBuilder.createQuery(Rarity.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username,card.get("rarity"));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}