package com.project.model.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cards {

	private List<Card> cards;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Card {

		private String id;

		private String name;

		private String imageUrlHiRes;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getImageUrlHiRes() {
			return imageUrlHiRes;
		}

		public void setImageUrl(String imageUrlHiRes) {
			this.imageUrlHiRes = imageUrlHiRes;
		}

	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

}
