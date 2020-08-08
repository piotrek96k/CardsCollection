package com.project.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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

	@ManyToMany(mappedBy = "cards")
	protected List<Account> accounts;
	
	public Card() {
	}

	public Card(@NotNull String id, @NotBlank String name, @NotBlank String imageUrl, @NotNull int cost,
			@NotNull Rarity rarity) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.cost = cost;
		this.rarity = rarity;
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