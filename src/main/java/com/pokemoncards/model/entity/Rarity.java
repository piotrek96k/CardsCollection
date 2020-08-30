package com.pokemoncards.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Rarity implements Comparable<Rarity>, Identifiable<String> {

	@Id
	@NotNull
	private String id;

	@NotNull
	@JsonIgnore
	private Integer value;

	@NotNull
	@Column(name = "sell_price")
	@JsonIgnore
	private Integer sellPrice;

	@Transient
	private long quantity;
	
	@Transient
	private long userQuantity;

	public Rarity(String id, Integer cost, Integer sellPrice) {
		this.id = id;
		this.value = cost;
		this.sellPrice = sellPrice;
	}
	
	public Rarity(Rarity rarity, long quantity, long userQuantity) {
		this(rarity, quantity);
		this.userQuantity = userQuantity;
	}

	public Rarity(Rarity rarity, long quantity) {
		this(rarity.id, rarity.value);
		this.quantity =  quantity;
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Integer sellPrice) {
		this.sellPrice = sellPrice;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getUserQuantity() {
		return userQuantity;
	}

	public void setUserQuantity(long userQuantity) {
		this.userQuantity = userQuantity;
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