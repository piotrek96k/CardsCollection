package com.project.model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.model.api.Cards;

@Service
public class ApiService {

	public static final String RESOURCE_URL;

	private static final RestTemplate REST_TEMPLATE;

	private static final String CARDS_STRING;

	private static int numberOfCards;

	private static int numberOfPages;

	static {
		RESOURCE_URL = "https://api.pokemontcg.io/v1/";
		CARDS_STRING = "cards";
		REST_TEMPLATE = new RestTemplate();
	}

	public class ApiData {

		private List<Cards.Card> cards;

		private Set<String> rarities;

		private ApiData(List<Cards.Card> cards, Set<String> rarities) {
			this.cards = cards;
			this.rarities = rarities;
		}

		public List<Cards.Card> getCards() {
			return Collections.unmodifiableList(cards);
		}

		public Set<String> getRarities() {
			return Collections.unmodifiableSet(rarities);
		}

	}

	public ApiData getApiData() {
		List<Cards.Card> cards = new ArrayList<Cards.Card>(getNumberOfCards());
		Set<String> rarities = new HashSet<String>();
		for (int i = 1; i <= getNumberOfPages(); i++)
			cards.addAll(getCardsByPage(i));
		for (Cards.Card card : cards) {
			if (card.getRarity() == null || card.getRarity().isBlank())
				card.setRarity("Common");
			rarities.add(card.getRarity());
		}
		return new ApiData(cards, rarities);
	}

	public List<Cards.Card> getAllCards() {
		List<Cards.Card> cards = new ArrayList<Cards.Card>(getNumberOfCards());
		for (int i = 1; i <= getNumberOfPages(); i++)
			cards.addAll(getCardsByPage(i));
		return cards;
	}

	public List<Cards.Card> getCardsByIds(List<String> ids) {
		StringBuilder builder = new StringBuilder(RESOURCE_URL);
		builder.append(CARDS_STRING);
		builder.append("?id=");
		String idsString = ids.toString();
		for (int i = 1; i < idsString.length() - 1; i++)
			if (idsString.charAt(i) != ' ')
				builder.append(idsString.charAt(i) == ',' ? '|' : idsString.charAt(i));
		Cards cards = REST_TEMPLATE.getForObject(builder.toString(), Cards.class);
		return cards.getCards();
	}

	public List<Cards.Card> getCardsByPage(int page) {
		StringBuilder builder = new StringBuilder(RESOURCE_URL);
		builder.append(CARDS_STRING);
		builder.append("?page=");
		builder.append(page);
		ResponseEntity<Cards> cardsEntity = REST_TEMPLATE.getForEntity(builder.toString(), Cards.class);
		return cardsEntity.getBody().getCards();
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
		ResponseEntity<Cards> cardsEntity = REST_TEMPLATE.getForEntity(RESOURCE_URL + CARDS_STRING, Cards.class);
		numberOfCards = Integer.parseInt(cardsEntity.getHeaders().get("Total-Count").get(0));
		numberOfPages = numberOfCards / 100 + (numberOfCards % 100 == 0 ? 0 : 1);
	}

}