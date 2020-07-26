package com.project.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.entity.Card;

public interface CardRepository extends JpaRepository<Card, Integer>{

	public Optional<Card> findByApiId(String apiId);
	
}
