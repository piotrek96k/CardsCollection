package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;
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

	@Override
	public Optional<Set> findById(String id, Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Set> criteriaQuery = criteriaBuilder.createQuery(Set.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username, card.get("set"));
		criteriaQuery.where(criteriaBuilder.equal(card.get("set").get("id"), id));
		TypedQuery<Set> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getResultList().size() > 0 ? Optional.of(typedQuery.getSingleResult()) : Optional.empty();
	}

}
