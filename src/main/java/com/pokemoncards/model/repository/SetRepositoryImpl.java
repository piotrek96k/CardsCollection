package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Set;

public class SetRepositoryImpl extends CardFieldRepositoryImpl<Set>{

	@Override
	public List<Set> findAll(Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Set> criteriaQuery = criteriaBuilder.createQuery(Set.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username,card.get("set"));
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}
