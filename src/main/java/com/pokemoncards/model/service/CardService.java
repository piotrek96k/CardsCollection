package com.pokemoncards.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.repository.CardRepository;
import com.pokemoncards.model.repository.RarityRepository;

@Service
public class CardService {

	private static final String LEGEND_RARITY_ID;

	static {
		LEGEND_RARITY_ID = "LEGEND";
	}

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private RarityRepository rarityRepository;

	public List<Card> getCards(Optional<String[]> ids, Optional<String> search) {
		if (ids.isEmpty())
			return getCardsWithEmptyIds(search);
		Function<String, Card> cardGetter;
		if (search.isEmpty() || cardRepository.countCardsBySearch(search.get()) == 0)
			cardGetter = id -> cardRepository.getNextCard(rarityRepository.findById(LEGEND_RARITY_ID).get(), id);
		else
			cardGetter = id -> cardRepository.getNextCard(search.get(), id);
		return getCards(cardGetter, ids.get());
	}

	private List<Card> getCardsWithEmptyIds(Optional<String> search) {
		int cardsQuantity;
		Function<Integer, Card> cardGetter;
		if (search.isEmpty()) {
			cardsQuantity = cardRepository.countCardsByRarity(LEGEND_RARITY_ID);
			cardGetter = number -> cardRepository.getCardByRowNumber(number,
					rarityRepository.findById(LEGEND_RARITY_ID).get());
		} else {
			cardsQuantity = cardRepository.countCardsBySearch(search.get());
			if (cardsQuantity == 0)
				return getCardsWithEmptyIds(Optional.empty());
			cardGetter = number -> cardRepository.getCardByRowNumber(number, search.get());
		}
		return getCards(cardGetter, cardsQuantity);
	}

	private List<Card> getCards(Function<Integer, Card> cardGetter, int cardsQuantity) {
		List<Card> cards = new ArrayList<Card>(5);
		int number = new Random().nextInt(cardsQuantity);
		for (int i = 0; i < 5; i++) {
			if (number == cardsQuantity)
				number = 0;
			cards.add(cardGetter.apply(number++));
		}
		return cards;
	}

	private List<Card> getCards(Function<String, Card> cardGetter, String[] ids) {
		List<Card> cards = new ArrayList<Card>();
		for (int i = 1; i < ids.length; i++)
			cards.add(cardRepository.findById(ids[i]).get());
		cards.add(cardGetter.apply(ids[ids.length - 1]));
		return cards;
	}

}