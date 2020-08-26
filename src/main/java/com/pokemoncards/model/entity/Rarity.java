package com.pokemoncards.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class Rarity implements Comparable<Rarity>, Identifiable<String> {

	@Id
	@NotNull
	private String id;

	@NotNull
	private Integer cost;

	@NotNull
	@Column(name = "sell_cost")
	private Integer sellCost;

	@Transient
	private int quantity;

	public Rarity(String id, Integer cost, Integer sellCost) {
		this.id = id;
		this.cost = cost;
		this.sellCost = sellCost;
	}

	public Rarity(Rarity rarity, long quantity) {
		this(rarity.id, rarity.cost);
		this.quantity = (int) quantity;
	}

	public Rarity(String id, Integer cost) {
		this(id, cost, 0);
	}

	public Rarity(String id) {
		this(id, 0);
	}

	public Rarity() {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	public Integer getSellCost() {
		return sellCost;
	}

	public void setSellCost(Integer sellCost) {
		this.sellCost = sellCost;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rarity other = (Rarity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Rarity other) {
		return id.compareTo(other.id);
	}

	@Override
	public String toString() {
		return id;
	}

}