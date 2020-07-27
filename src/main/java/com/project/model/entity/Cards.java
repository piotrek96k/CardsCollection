package com.project.model.entity;

import java.math.BigInteger;

public class Cards extends Card {

	private int quantity;

	public Cards(String id, String name, Integer cost, String imageUrl, String rarity, BigInteger quantity) {
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.imageUrl = imageUrl;
		this.rarity = new Rarity(rarity);
		this.quantity = quantity.intValue();
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return super.toString() + "Cards [quantity=" + quantity + "]";
	}

}
