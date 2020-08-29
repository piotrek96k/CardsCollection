package com.pokemoncards.model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pokemoncards.model.api.Cards;
import com.pokemoncards.model.api.Sets;
import com.pokemoncards.model.api.Types;

@Service
public class ApiService {

	private static final String RESOURCE_URL;

	private static final RestTemplate REST_TEMPLATE;

	private static final String CARDS;

	private static final String SETS;

	private static final String TYPES;

	private static int numberOfCards;

	private static int numberOfPages;

	static {
		RESOURCE_URL = "https://api.pokemontcg.io/v1/";
		CARDS = "cards";
		SETS = "sets";
		TYPES = "types";
		REST_TEMPLATE = new RestTemplate();
	}

	public class ApiData {

		private List<Cards.Card> cards;

		private Set<String> rarities;

		private List<Sets.Set> sets;

		private List<String> types;

		private ApiData(List<Cards.Card> cards, Set<String> rarities, List<Sets.Set> sets, List<String> types) {
			this.cards = cards;
			this.rarities = rarities;
			this.sets = sets;
			this.types = types;
		}

		public List<Cards.Card> getCards() {
			return Collections.unmodifiableList(cards);
		}

		public Set<String> getRarities() {
			return Collections.unmodifiableSet(rarities);
		}

		public List<Sets.Set> getSets() {
			return Collections.unmodifiableList(sets);
		}

		public List<String> getTypes() {
			return Collections.unmodifiableList(types);
		}

	}

	public ApiData getApiData() {
		List<Cards.Card> cards = getAllCards();
		Set<String> rarities = getAllRarities(cards);
		List<Sets.Set> sets = getAllSets();
		List<String> types = getAllTypes();
		return new ApiData(cards, rarities, sets, types);
	}

	private List<Cards.Card> getAllCards() {
		List<Cards.Card> cards = new ArrayList<Cards.Card>(getNumberOfCards());
		for (int i = 1; i <= getNumberOfPages(); i++)
			cards.addAll(getCardsByPage(i));
		return cards;
	}

	private Set<String> getAllRarities(List<Cards.Card> cards) {
		Set<String> rarities = new HashSet<String>();
		for (Cards.Card card : cards)
			rarities.add(card.getRarity());
		return rarities;
	}

	private List<Sets.Set> getAllSets() {
		ResponseEntity<Sets> setsEntity = REST_TEMPLATE.getForEntity(RESOURCE_URL + SETS, Sets.class);
		return setsEntity.getBody().getSets();
	}

	private List<String> getAllTypes() {
		ResponseEntity<Types> typesEntity = REST_TEMPLATE.getForEntity(RESOURCE_URL + TYPES, Types.class);
		List<String> types = typesEntity.getBody().getTypes();
		types.add("None");
		return types;
	}

	public List<Cards.Card> getCardsByPage(int page) {
		StringBuilder builder = new StringBuilder(RESOURCE_URL);
		builder.append(CARDS);
		builder.append("?page=");
		builder.append(page);
		ResponseEntity<Cards> cardsEntity = REST_TEMPLATE.getForEntity(builder.toString(), Cards.class);
		List<Cards.Card> cards = cardsEntity.getBody().getCards();
		correctCards(cards);
		return cards;
	}

	private void correctCards(List<Cards.Card> cards) {
		for (Cards.Card card : cards) {
			if (card.getRarity() == null || card.getRarity().isBlank())
				card.setRarity("Common");
			if(card.getTypes() == null || card.getTypes().isEmpty()) {
				Set<String> types = new HashSet<String>();
				types.add("None");
				card.setTypes(types);
			}
			if (card.getEvolvesFrom() != null && (card.getEvolvesFrom().isEmpty() || card.getEvolvesFrom().isBlank()))
				card.setEvolvesFrom(null);
			if (card.getHp() != null
					&& (card.getHp().equals("None") || card.getHp().isEmpty() || card.getHp().isBlank()))
				card.setHp(null);
		}
	}

	public int getNumberOfCards() {
		if (numberOfCards == 0)
			readNumberOfCardsAndPages();
		return numberOfCards;
	}

	public int getNumberOfPages() {
		if (numberOfPages == 0)
			readNumberOfCardsAndPages();
		return numberOfPages;
	}

	private void readNumberOfCardsAndPages() {
		ResponseEntity<Cards> cardsEntity = REST_TEMPLATE.getForEntity(RESOURCE_URL + CARDS, Cards.class);
		numberOfCards = Integer.parseInt(cardsEntity.getHeaders().get("Total-Count").get(0));
		numberOfPages = numberOfCards / 100 + (numberOfCards % 100 == 0 ? 0 : 1);
	}

}