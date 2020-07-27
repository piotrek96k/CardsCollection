package com.project.model.repository;

import org.springframework.beans.factory.annotation.Autowired;

public class CardRepositoryImpl implements CardQuery {

	@Autowired
	private CardRepository cardRepository;

	@Override
	public int getNumberOfPages() {
		int cards = (int) cardRepository.count();
		return cards / 100 + (cards % 100 == 0 ? 0 : 1);
	}

}