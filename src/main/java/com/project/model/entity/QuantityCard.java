package com.project.model.entity;

import java.math.BigInteger;

public class QuantityCard extends Card {

	protected int quantity;

	public QuantityCard(String id, String name, String imageUrl, Integer cost, String rarity) {
		super(id, name, imageUrl, cost.intValue(), new Rarity(rarity));
	}

	public QuantityCard(String id, String name, String imageUrl, Integer cost, String rarity, BigInteger quantity) {
		this(id, name, imageUrl, cost, rarity);
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
