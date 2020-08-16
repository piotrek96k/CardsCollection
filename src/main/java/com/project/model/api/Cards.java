package com.project.model.api;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
		
		private Set<String> types;
		
		@JsonProperty("nationalPokedexNumber")
		private Integer pokedexNumber;
		
		private String evolvesFrom;
		
		private String hp;
		
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

		public Set<String> getTypes() {
			return types;
		}

		public void setTypes(Set<String> types) {
			this.types = types;
		}

		public Integer getPokedexNumber() {
			return pokedexNumber;
		}

		public void setPokedexNumber(Integer pokedexNumber) {
			this.pokedexNumber = pokedexNumber;
		}

		public String getEvolvesFrom() {
			return evolvesFrom;
		}

		public void setEvolvesFrom(String evolvesFrom) {
			this.evolvesFrom = evolvesFrom;
		}

		public String getHp() {
			return hp;
		}

		public void setHp(String hp) {
			this.hp = hp;
		}

	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

}