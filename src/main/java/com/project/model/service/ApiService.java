package com.project.model.service;

import java.util.ArrayList;
import java.util.List;

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