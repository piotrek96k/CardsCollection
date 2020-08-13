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

		private String imageUrl;

		private String rarity;
		
		private String set;
		
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

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getRarity() {
			return rarity;
		}

		public void setRarity(String rarity) {
			this.rarity = rarity;
		}

		public String getSet() {
			return set;
		}

		public void setSet(String set) {
			this.set = set;
		}

	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

}