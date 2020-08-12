package com.project.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Card {

	@Id
	@NotNull
	protected String id;

	@NotBlank
	protected String name;

	@NotBlank
	protected String imageUrl;

	@NotNull
	protected int cost;

	@NotNull
	@ManyToOne
	protected Rarity rarity;

	@Transient
	protected int quantity;

	@ManyToMany(mappedBy = "cards")
	protected List<Account> accounts;

	public Card() {
	}

	public Card(String id, String name, String imageUrl, int cost, Rarity rarity) {
		this(id, name, imageUrl, cost, rarity, 0);
	}
	
	public Card (Card card, long quantity) {
		this(card.id, card.name, card.imageUrl, card.cost, card.rarity, quantity);
	}

	public Card(String id, String name, String imageUrl, int cost, Rarity rarity, long quantity) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.cost = cost;
		this.rarity = rarity;
		this.quantity = (int)quantity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
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

	public Rarity getRarity() {
		return rarity;
	}

	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", name=" + name + ", imageUrl=" + imageUrl + ", cost=" + cost + "]";
	}

}