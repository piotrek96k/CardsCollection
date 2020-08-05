package com.project.model.entity;

import java.math.BigInteger;

public class QuantityCard extends Card {

	protected int quantity;

	public QuantityCard(String id, String name, Integer cost, String imageUrl, String rarity, BigInteger quantity) {
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.imageUrl = imageUrl;
		this.rarity = new Rarity(rarity);
		this.quantity = quantity.intValue();
	}

	public QuantityCard(Card card) {
		this(card, 0);
	}

	public QuantityCard(Card card, int quantity) {
		id = card.getId();
		name = card.getName();
		cost = card.getCost();
		imageUrl = card.getImageUrl();
		rarity = card.getRarity();
		this.quantity = quantity;
	}

	public QuantityCard() {

	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return super.toString() + "QuantityCard [quantity=" + quantity + "]";
	}

}
